package com.mjdi.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class SmsCheckService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userCode = request.getParameter("code");
        Integer realCode = (Integer)request.getSession().getAttribute("savedAuthNum");
        
        if(realCode != null && userCode.equals(String.valueOf(realCode))) {
            response.getWriter().write("ok");
        } else {
            response.getWriter().write("fail");
        }
    }
}