package com.mjdi.user;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;
import com.mjdi.util.DBM;

public class UserUpdateService implements Action {
    
    // 프로필 등록 신청 비용 (50 P)
    private static final int PROFILE_CHANGE_COST = 50; 

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. 파라미터 및 세션 정보 추출
        request.setCharacterEncoding("UTF-8");
        String uName = request.getParameter("name");
        String uPhone = request.getParameter("phone");
        String uEmail = request.getParameter("email");
        String uNewPw = request.getParameter("newPw");
        String uProfile = request.getParameter("profile"); // 새로 선택된 프로필 파일명
        
        UserDTO currentUser = (UserDTO)request.getSession().getAttribute("sessionUser");
        if (currentUser == null) {
             response.sendRedirect("login.jsp"); return;
        }

        String userId = currentUser.getJdi_user();
        String oldProfile = currentUser.getJdi_profile(); // 기존 프로필 파일명

        // DAO 인스턴스 준비
        UserDAO dao = UserDAO.getInstance(); // 싱글톤 권장
        PointDAO pointDao = PointDAO.getInstance();
        
        // 2. 프로필 사진 변경 여부 확인
        boolean isProfileChanged = (uProfile != null && !uProfile.equals(oldProfile));
        
        // [검증] 포인트 잔액 확인 (트랜잭션 시작 전 미리 체크)
        if (isProfileChanged) {
            if (!pointDao.checkPointSufficient(userId, PROFILE_CHANGE_COST)) {
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().write("<script>alert('새 프로필 등록 신청 비용 " + PROFILE_CHANGE_COST + " P가 부족합니다!'); location.href='mypage.jsp';</script>");
                return;
            }
        }
        
        Connection conn = null;

        try {
            // 3. 트랜잭션 시작
            conn = DBM.getConnection();
            conn.setAutoCommit(false); // 자동 커밋 끄기

            // 4. 포인트 차감 (프로필 변경 시에만)
            if (isProfileChanged) {
                // Connection을 넘겨주는 메서드 사용
                int logResult = pointDao.addPoint(conn, userId, -PROFILE_CHANGE_COST, "프로필 이미지 변경");
                if (logResult <= 0) throw new Exception("포인트 차감 실패");
            }

            // 5. 회원 정보 업데이트 (Connection을 넘겨주는 메서드 사용)
            // ※ UserDAO에 updateAllWithConn 메서드 추가 필요
            int updateRes = dao.updateAllWithConn(conn, userId, uName, uPhone, uEmail, uNewPw, uProfile);
            if (updateRes <= 0) throw new Exception("회원 정보 수정 실패");

            // 6. 모두 성공 시 커밋
            conn.commit();

            // 7. 세션 정보 갱신 (DB 반영 성공 후에만 실행)
            currentUser.setJdi_name(uName);
            currentUser.setJdi_phone(uPhone);
            currentUser.setJdi_email(uEmail);
            currentUser.setJdi_profile(uProfile);
            // 비밀번호 변경 여부는 보안상 세션에 담지 않으므로 생략 가능하나 필요시 로직 추가
            
            request.getSession().setAttribute("sessionUser", currentUser);
            request.getSession().removeAttribute("isPwdChecked"); // 비밀번호 재확인 세션 삭제

            response.setContentType("text/html; charset=UTF-8");
            String msg = isProfileChanged ? PROFILE_CHANGE_COST + " P가 차감되었으며, 정보가 수정되었습니다." : "정보가 수정되었습니다.";
            response.getWriter().write("<script>alert('" + msg + "'); location.href='mypage.jsp';</script>");

        } catch (Exception e) {
            // 8. 실패 시 롤백
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) {}
            }
            e.printStackTrace();
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write("<script>alert('처리 중 오류가 발생했습니다.'); history.back();</script>");
        } finally {
            // 9. 연결 종료
            DBM.close(conn, null, null);
        }
    }
}