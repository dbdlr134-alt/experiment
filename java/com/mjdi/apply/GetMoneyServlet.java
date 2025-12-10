package com.mjdi.apply;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mjdi.user.UserDTO;
import com.mjdi.user.PointDAO;

// 팩토리(Factory)를 타지 않고 바로 호출되는 독립 서블릿
@WebServlet("/GetMoney")
public class GetMoneyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        // 1. 로그인 체크 (세션 확인)
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("sessionUser");

        if (user == null) {
            response.getWriter().write("FAIL:LOGIN_REQUIRED");
            return;
        }

        // 2. 포인트 지급 로직
        // 보내주신 DAO의 addPoint 메서드를 그대로 사용합니다.
        // 로그 방식이므로 INSERT만 하면 getTotalPoint에서 자동으로 합산됩니다.
        int amount = 10000; // 10,000 포인트
        String reason = "쇼미더머니(치트)";
        
        PointDAO dao = PointDAO.getInstance();
        int result = dao.addPoint(user.getJdi_user(), amount, reason);

        // 3. 결과 응답
        if (result > 0) {
            response.getWriter().write("SUCCESS:" + amount);
        } else {
            response.getWriter().write("FAIL:DB_ERROR");
        }
    }
}