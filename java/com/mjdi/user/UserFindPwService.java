package com.mjdi.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class UserFindPwService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pId = request.getParameter("id");
        String pName = request.getParameter("name");
        String pPhone = request.getParameter("phone");
        
        String tempPw = java.util.UUID.randomUUID().toString().substring(0, 8);
        
        UserDAO dao = new UserDAO();
        int updateCount = dao.resetPasswordByPhone(pId, pName, pPhone, tempPw);
        
        response.setContentType("text/html; charset=UTF-8");
        if(updateCount > 0) {
            String msg = "[My J-Dic] 임시 비밀번호는 [" + tempPw + "] 입니다. 로그인 후 변경하세요.";
            dao.sendSmsMessage(pPhone, msg);
            response.getWriter().write("<script>alert('회원님의 휴대폰으로 임시 비밀번호를 전송했습니다.'); location.href='login.jsp';</script>");
        } else {
            response.getWriter().write("<script>alert('일치하는 회원 정보가 없습니다.'); history.back();</script>");
        }
    }
}