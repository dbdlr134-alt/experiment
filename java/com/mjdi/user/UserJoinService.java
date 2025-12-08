package com.mjdi.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class UserJoinService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDTO joinDto = new UserDTO(
            request.getParameter("id"),
            request.getParameter("pw"),
            request.getParameter("name"),
            request.getParameter("email"),
            request.getParameter("phone")
        );
        
        UserDAO dao = new UserDAO();
        int result = dao.joinUser(joinDto);
        
        if(result > 0) {
            response.sendRedirect("login.jsp");
        } else {
            response.sendRedirect("join.jsp?error=fail");
        }
    }
}