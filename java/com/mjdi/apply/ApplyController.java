package com.mjdi.apply;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

@WebServlet("*.apply")
public class ApplyController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 1. URI에서 명령어(cmd) 추출
        //    예) URI: /MyJDic/approveEdit.apply
        //        ctx: /MyJDic
        //        cmd: /approveEdit.apply
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        String cmd = uri.substring(ctx.length());

        System.out.println(">> ApplyController 요청: " + cmd);

        // 2. 팩토리에서 액션 가져오기
        ApplyServiceFactory factory = ApplyServiceFactory.getInstance();
        Action action = factory.getAction(cmd);

        // 3. 액션 실행
        if (action != null) {
            action.process(request, response);
        } else {
            System.out.println(">> 잘못된 요청입니다: " + cmd);
            response.sendRedirect("index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
