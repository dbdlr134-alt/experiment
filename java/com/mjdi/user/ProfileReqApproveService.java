package com.mjdi.user;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mjdi.util.Action;
import com.mjdi.util.DBM;

public class ProfileReqApproveService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // 1. [보안] 관리자 권한 체크 (필수)
        HttpSession session = request.getSession();
        UserDTO sessionUser = (UserDTO) session.getAttribute("sessionUser");
        
        // "admin" 역할이 있는지 확인 (UserDTO 구조에 따라 수정 필요)
        // 예시: sessionUser가 없거나 admin이 아니면 튕겨냄
        if (sessionUser == null || !"admin".equals(sessionUser.getJdi_user())) { 
            response.getWriter().write("<script>alert('관리자만 접근 가능합니다.'); location.href='main.jsp';</script>");
            return;
        }

        int reqId = 0;
        try {
            reqId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.getWriter().write("<script>history.back();</script>");
            return;
        }

        ProfileReqDAO reqDao = ProfileReqDAO.getInstance();
        PointDAO pointDao = PointDAO.getInstance();
        UserDAO userDao = UserDAO.getInstance();

        Connection conn = null;

        try {
            // 2. DB 연결 시작 (트랜잭션 시작)
            conn = DBM.getConnection();
            conn.setAutoCommit(false); // 자동 커밋 끄기

            // 3. 신청 정보 가져오기
            // (주의: reqDao.getRequest도 Connection을 받는 버전이 좋으나, 읽기 전용이라 기존 것 사용 가능)
            ProfileReqDTO dto = reqDao.getRequest(reqId); 
            if (dto == null) {
                throw new Exception("존재하지 않는 신청입니다.");
            }
            if (!"PENDING".equals(dto.getStatus())) {
                throw new Exception("이미 처리된 요청입니다.");
            }

            String targetUserId = dto.getUserId();
            String newProfilePath = dto.getImagePath();
            int cost = 50;

            // 4. [검증] 포인트 잔액 확인 (PointDAO 활용)
            if (!pointDao.checkPointSufficient(targetUserId, cost)) {
                // 잔액 부족 시 예외 발생 -> 롤백 및 거부 처리
                throw new Exception("유저의 포인트가 부족하여 승인할 수 없습니다.");
            }

            // 5. [실행] 트랜잭션 내 로직 수행 (순서 중요)
            
            // 5-1. 포인트 차감 (Connection 전달)
            pointDao.addPoint(conn, targetUserId, -cost, "프로필 이미지 변경 승인");
            
            // 5-2. 유저 프로필 업데이트 (UserDAO에 conn 받는 메서드 필요)
            // userDao.updateProfile(conn, targetUserId, newProfilePath); 
            // ※ 만약 UserDAO 수정이 어렵다면, 여기서 직접 SQL을 실행하거나 기존 메서드 사용(위험 감수)해야 합니다.
            // 안전을 위해 UserDAO에도 updateProfile(Connection conn, ...)을 만드십시오.
            userDao.updateProfileWithConn(conn, targetUserId, newProfilePath); 

            // 5-3. 신청 상태 변경 (ProfileReqDAO에 conn 받는 메서드 필요)
            reqDao.updateStatusWithConn(conn, reqId, "APPROVED");

            // 6. 성공 시 커밋
            conn.commit();
            
            response.getWriter().write(
                "<script>alert('승인 완료 (포인트 50 차감)'); location.href='profileReqList.do';</script>"
            );

        } catch (Exception e) {
            // 7. 실패 시 롤백
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) {}
            }
            e.printStackTrace();
            String msg = e.getMessage().replace("'", ""); // JS 에러 방지
            response.getWriter().write("<script>alert('오류 발생: " + msg + "'); history.back();</script>");
            
        } finally {
            // 8. 연결 종료
            DBM.close(conn, null, null); 
        }
    }
}