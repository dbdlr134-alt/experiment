package com.mjdi.qna;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;



/**
 * Servlet implementation class QnAController
 */
@WebServlet("/QnAController")
public class QnAController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // 1. cmd 파라미터 받기
        String cmd = request.getParameter("cmd");
        if(cmd == null) cmd = "qna_list"; // cmd가 없으면 기본적으로 목록 보여주기
        
        System.out.println(">> QnAController 요청: " + cmd);
        
        // 2. 공장(Factory)한테서 일꾼(Action) 받아오기
        // (이름을 WordServiceFactory랑 비슷하게 QnAServiceFactory로 지을게)
        QnAServiceFactory factory = QnAServiceFactory.getInstance();
        Action action = factory.getAction(cmd);
        
        // 3. 일꾼 실행
        if(action != null) {
            // WordController 보니까 execute가 아니라 process 메서드 쓰는 것 같네? 맞춰줄게!
            action.process(request, response); 
        } else {
            System.out.println(">> 잘못된 요청입니다: " + cmd);
            response.sendRedirect("index.jsp"); // 이상하면 메인으로 쫓아내기
        }
    }

}
