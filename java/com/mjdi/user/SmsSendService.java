package com.mjdi.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mjdi.util.Action;

public class SmsSendService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String phone = request.getParameter("phone");
        UserDAO dao = new UserDAO();
        int authNum = dao.sendSmsAndGetCode(phone);
        
        HttpSession session = request.getSession();
        session.setAttribute("savedAuthNum", authNum);
        
        response.getWriter().write("success");
    }
}