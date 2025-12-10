package com.mjdi.user;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;
import com.mjdi.util.DBM;

public class ThemeBuyService implements Action {
    
    private static final int DEFAULT_PRICE = 100; 

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        
        UserDTO user = (UserDTO) request.getSession().getAttribute("sessionUser");
        if (user == null) {
            response.sendRedirect("login.jsp"); return;
        }

        String themeCode = request.getParameter("theme");
        String userId = user.getJdi_user();
        
        PointDAO pDao = PointDAO.getInstance();
        UserDAO uDao = UserDAO.getInstance();

        // 1. [검증] 이미 보유중인지 확인 (읽기 작업이라 트랜잭션 밖에서 해도 무방)
        if(uDao.getMyThemes(userId).contains(themeCode)) {
            response.getWriter().write("<script>alert('이미 보유한 테마입니다.'); history.back();</script>");
            return;
        }

        // 2. [검증] 포인트 부족 확인
        if(!pDao.checkPointSufficient(userId, DEFAULT_PRICE)) {
            response.getWriter().write("<script>alert('포인트가 부족합니다. (" + DEFAULT_PRICE + "P 필요)'); history.back();</script>");
            return;
        }

        Connection conn = null;
        try {
            // 3. 트랜잭션 시작
            conn = DBM.getConnection();
            conn.setAutoCommit(false); // 자동 커밋 끄기 (핵심)

            // 4. 포인트 차감 (Connection을 넘겨줌)
            // pDao.addPoint 메서드가 예외를 던지도록 설계되어 있어야 함
            int resultPoint = pDao.addPoint(conn, userId, -DEFAULT_PRICE, "테마 구매 (" + themeCode + ")");
            if (resultPoint == 0) throw new Exception("포인트 차감 실패");

            // 5. 테마 구매 등록 (Connection을 넘겨주는 새 메서드 필요)
            // UserDAO에 buyThemeWithConn 메서드를 추가해야 합니다.
            int resultTheme = uDao.buyThemeWithConn(conn, userId, themeCode);
            if (resultTheme == 0) throw new Exception("테마 등록 실패");

            // 6. 모두 성공하면 커밋
            conn.commit();
            
            // 7. 성공 페이지 이동
            String redirectUrl = request.getContextPath() + "/themeApply.do?theme=" + themeCode;
            response.getWriter().write("<script>alert('구매 완료! 테마를 적용합니다.'); location.href='" + redirectUrl + "';</script>");

        } catch (Exception e) {
            // 8. 실패 시 롤백 (포인트 복구)
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) {}
            }
            e.printStackTrace();
            response.getWriter().write("<script>alert('구매 처리 중 오류가 발생했습니다.'); history.back();</script>");
        } finally {
            // 9. 연결 종료
            DBM.close(conn, null, null);
        }
    }
}