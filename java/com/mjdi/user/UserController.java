package com.mjdi.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

@WebServlet("*.do")
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024,      // 임시 파일로 저장되기 전 메모리 버퍼 크기
	    maxFileSize = 1024 * 1024 * 20,        // 파일 하나 최대 5MB (원하는 대로 조정)
	    maxRequestSize = 1024 * 1024 * 20     // 전체 요청 최대 10MB
	)
public class UserController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        String command = uri.substring(ctx.length());
        
        System.out.println(">> UserController 요청: " + command);
        
        UserServiceFactory factory = UserServiceFactory.getInstance();
        Action action = factory.getAction(command);
        
        if (action != null) {
            action.process(request, response);
        } else {
            System.out.println(">> 잘못된 요청입니다: " + command);
            response.sendRedirect("index.jsp");
        }
    }
}