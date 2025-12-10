package com.mjdi.apply;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mjdi.user.PointDAO;
import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;
import com.mjdi.util.DBM;
import com.mjdi.word.WordDAO;
import com.mjdi.word.WordDTO;

public class ApplyApproveService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 0. [보안] 관리자 권한 체크 (선택 사항이나 권장됨)
        HttpSession session = request.getSession();
        UserDTO sessionUser = (UserDTO) session.getAttribute("sessionUser");
        // 관리자가 아니면 튕겨내는 로직 (필요시 주석 해제하여 사용)
        if (sessionUser == null || !"admin".equals(sessionUser.getJdi_user())) {
             response.sendRedirect("login.jsp");
             return;
        }

        int reqId = 0;
        try {
            reqId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect("adminList.apply");
            return;
        }

        ApplyDAO applyDao = ApplyDAO.getInstance();
        WordDAO wordDao = WordDAO.getInstance();
        PointDAO pointDao = PointDAO.getInstance();
        
        Connection conn = null;

        try {
            // 1. 트랜잭션 시작
            conn = DBM.getConnection();
            conn.setAutoCommit(false); // 자동 커밋 끄기

            // 신청 정보 가져오기 (읽기는 트랜잭션 밖에서도 가능하나, 안에서 해도 무방)
            ApplyDTO reqDto = applyDao.getApply(reqId);
            
            if(reqDto != null) {
                // 2. 단어장에 등록 (Connection 전달)
                WordDTO wordDto = new WordDTO();
                wordDto.setWord(reqDto.getWord());
                wordDto.setDoc(reqDto.getDoc());
                wordDto.setKorean(reqDto.getKorean());
                wordDto.setJlpt(reqDto.getJlpt());
                
                // WordDAO에 insertWordWithConn 메서드 필요
                int wordResult = wordDao.insertWordWithConn(conn, wordDto);
                if (wordResult == 0) throw new Exception("단어 등록 실패");
                
                // 3. 신청 상태 변경 (Connection 전달)
                // ApplyDAO에 updateStatusWithConn 메서드 필요
                int statusResult = applyDao.updateStatusWithConn(conn, reqId, "OK");
                if (statusResult == 0) throw new Exception("상태 변경 실패");

                // 4. 포인트 지급 (Connection 전달)
                // PointDAO.addPoint(Connection conn, ...)은 이미 만들었음
                pointDao.addPoint(conn, reqDto.getJdiUser(), 50, "단어등록 승인 보상");
                
                // 5. 모든 작업 성공 시 커밋
                conn.commit();
            }

        } catch (Exception e) {
            // 6. 실패 시 롤백
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) {}
            }
            e.printStackTrace();
            // 에러 발생 시 처리 (alert 등)
        } finally {
            // 7. 연결 종료
            DBM.close(conn, null, null);
        }

        response.sendRedirect("adminList.apply");
    }
}