package com.mjdi.user;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson; // Gson 라이브러리 필요
import com.mjdi.util.Action;

public class UserForbiddenCheckService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDAO dao = new UserDAO();
        ArrayList<String> forbidden = dao.getForbiddenWords();
        
        // JSON으로 보내기
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(new Gson().toJson(forbidden));
    }
}
