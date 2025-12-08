package com.mjdi.word;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class WordSearchService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        WordDAO dao = WordDAO.getInstance();
        
        // 1. 단어 검색 실행
        ArrayList<WordDTO> list = dao.searchWords(query);

        // 2. 결과 저장
        request.setAttribute("wordList", list);
        request.setAttribute("searchQuery", query);
        
        // 3. 페이지 이동
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}