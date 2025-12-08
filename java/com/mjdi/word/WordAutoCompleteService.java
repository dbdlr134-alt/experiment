package com.mjdi.word;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.mjdi.util.Action;

public class WordAutoCompleteService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String key = request.getParameter("key");
        WordDAO dao = WordDAO.getInstance();
        List<WordDTO> list = dao.autoComplete(key);

        Gson gson = new Gson();
        String json = gson.toJson(list);

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }
}