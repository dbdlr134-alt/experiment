package com.mjdi.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class UserCheckPassService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String inputPw = request.getParameter("pw");
        UserDTO sessUser = (UserDTO)request.getSession().getAttribute("sessionUser");
        
        UserDAO dao = new UserDAO();
        if(dao.checkPassword(sessUser.getJdi_user(), inputPw)) {
            request.getSession().setAttribute("isPwdChecked", "ok");
            response.sendRedirect("edit_profile.jsp");
        } else {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write("<script>alert('비밀번호가 틀렸습니다.'); location.href='pwd_check.jsp';</script>");
        }
    }
}