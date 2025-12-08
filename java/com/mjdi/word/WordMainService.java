package com.mjdi.word;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.quiz.QuizDAO; // [중요] 퀴즈 DAO 임포트
import com.mjdi.util.Action;

public class WordMainService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        QuizDAO.getInstance().checkAndSetGlobalQuiz(request.getServletContext());
        
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}