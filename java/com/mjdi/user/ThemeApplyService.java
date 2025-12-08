package com.mjdi.user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mjdi.util.Action;

public class ThemeApplyService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDTO user = (UserDTO) request.getSession().getAttribute("sessionUser");
        if (user == null) {
            response.sendRedirect("login.jsp"); return;
        }

        String themeCode = request.getParameter("theme");
        UserDAO.getInstance().updateTheme(user.getJdi_user(), themeCode);
        
        // 세션 정보도 업데이트 (즉시 반영을 위해)
        user.setJdi_theme(themeCode);
        request.getSession().setAttribute("sessionUser", user);

        // 적용 완료 후 마이페이지로 이동 (절대 경로 사용)
        response.sendRedirect(request.getContextPath() + "/mypage.jsp");
    }
}