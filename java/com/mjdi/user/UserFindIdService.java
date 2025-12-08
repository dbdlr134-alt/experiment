package com.mjdi.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class UserFindIdService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fName = request.getParameter("name");
        String fEmail = request.getParameter("email");
        
        UserDAO dao = new UserDAO();
        String foundId = dao.findId(fName, fEmail);
        
        response.setContentType("text/html; charset=UTF-8");
        if(foundId != null) {
            response.getWriter().write("<script>alert('회원님의 아이디는 [" + foundId + "] 입니다.'); location.href='login.jsp';</script>");
        } else {
            response.getWriter().write("<script>alert('일치하는 정보가 없습니다.'); history.back();</script>");
        }
    }
}