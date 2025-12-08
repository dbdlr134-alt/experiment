package com.mjdi.word;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

@WebServlet("/WordController")
public class WordController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String cmd = request.getParameter("cmd");
        if(cmd == null) cmd = "main"; // 기본값
        
        System.out.println(">> WordController 요청: " + cmd);
        
        WordServiceFactory factory = WordServiceFactory.getInstance();
        Action action = factory.getAction(cmd);
        
        if(action != null) {
            action.process(request, response);
        } else {
            System.out.println(">> 잘못된 요청입니다: " + cmd);
            response.sendRedirect("index.jsp");
        }
    }
}