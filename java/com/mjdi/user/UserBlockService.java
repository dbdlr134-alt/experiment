package com.mjdi.user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mjdi.util.Action;

public class UserBlockService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDTO admin = (UserDTO)request.getSession().getAttribute("sessionUser");
        if(admin == null || !"ADMIN".equals(admin.getJdi_role())) {
            response.sendRedirect("index.jsp"); return;
        }

        String targetId = request.getParameter("id");
        String action = request.getParameter("action"); // 'block' 또는 'active'
        String newStatus = "block".equals(action) ? "BLOCK" : "ACTIVE";
        
        UserDAO.getInstance().updateUserStatus(targetId, newStatus);
        
        // 처리 후 목록으로 복귀
        response.sendRedirect(request.getContextPath() + "/userList.do");
    }
}