package com.mjdi.quiz;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

@WebServlet("/QuizController")
public class QuizController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String cmd = request.getParameter("cmd");
        System.out.println(">> QuizController 요청: " + cmd);
        
        QuizServiceFactory factory = QuizServiceFactory.getInstance();
        Action action = factory.getAction(cmd);
        
        if(action != null) {
            action.process(request, response);
        } else {
            System.out.println(">> 잘못된 퀴즈 요청입니다: " + cmd);
            response.sendRedirect("index.jsp");
        }
    }
}