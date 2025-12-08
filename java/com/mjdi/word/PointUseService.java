package com.mjdi.word;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.user.PointDAO;
import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class PointUseService implements Action {
    
    // 포인트 사용에 필요한 비용
    private static final int COST_POINT = 100; 

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDTO user = (UserDTO) request.getSession().getAttribute("sessionUser");
        String userId = (user != null) ? user.getJdi_user() : null;
        
        // 1. 로그인 체크
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        PointDAO pointDao = PointDAO.getInstance();
        
        // 2. 포인트 잔액 확인
        if (!pointDao.checkPointSufficient(userId, COST_POINT)) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write("<script>alert('포인트가 부족합니다! (필요: " + COST_POINT + " P)'); history.back();</script>");
            return;
        }

        // 3. 포인트 차감 실행 (음수 값 사용)
        // [핵심] amount를 -COST_POINT로 설정하여 차감 내역을 log 테이블에 기록합니다.
        int result = pointDao.addPoint(userId, -COST_POINT, "단어 요청 우선권 사용"); 
        
        response.setContentType("text/html; charset=UTF-8");
        if (result > 0) {
            // 4. 성공 시 처리 (성공 메시지 + 마이페이지 또는 메인으로 이동)
            response.getWriter().write("<script>alert('포인트가 성공적으로 차감되었습니다! (" + COST_POINT + " P 사용)'); location.href='" + request.getContextPath() + "/mypage.jsp';</script>");
        } else {
            // 5. 실패 시 처리 (DB 오류 등)
            response.getWriter().write("<script>alert('포인트 사용 중 DB 오류가 발생했습니다.'); history.back();</script>");
        }
    }
}