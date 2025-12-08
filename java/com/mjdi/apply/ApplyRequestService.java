package com.mjdi.apply;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class ApplyRequestService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDTO user = (UserDTO)request.getSession().getAttribute("sessionUser");
        if(user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        ApplyDTO dto = new ApplyDTO(
            request.getParameter("word"),
            request.getParameter("doc"),
            request.getParameter("korean"),
            request.getParameter("jlpt"),
            user.getJdi_user()
        );
        
        int result = ApplyDAO.getInstance().insertApply(dto);
        
        response.setContentType("text/html; charset=UTF-8");
        if(result > 0) {
            response.getWriter().write("<script>alert('단어 신청이 완료되었습니다.'); location.href='mypage.jsp';</script>");
        } else {
            response.getWriter().write("<script>alert('신청 실패.'); history.back();</script>");
        }
    }
}