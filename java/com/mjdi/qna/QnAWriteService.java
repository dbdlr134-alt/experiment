package com.mjdi.qna;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class QnAWriteService implements Action {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. 세션 가져오기
        HttpSession session = request.getSession();
        
        // 2. 로그인 여부 확인 (UserDTO가 세션에 있는지)
        // 마이페이지 코드 보니까 'sessionUser'라는 이름으로 저장하네?
        UserDTO myUser = (UserDTO) session.getAttribute("sessionUser");
        
        if (myUser == null) {
            // 비회원이다! 쫓아내!
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('로그인후 사용 가능한 서비스입니다.');");
            out.println("location.href='login.jsp';"); // 로그인 페이지로 이동
            out.println("</script>");
            out.close();
            return; // 여기서 끝냄
        }

        // 3. 회원이다! 글쓰기 화면으로 이동
        String path = "/qna_write.jsp"; 
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }

}