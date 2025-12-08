package com.mjdi.user;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mjdi.util.Action;

public class MessageSendService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDTO admin = (UserDTO)request.getSession().getAttribute("sessionUser");
        // 관리자 체크
        if(admin == null || !"ADMIN".equals(admin.getJdi_role())) {
            response.sendRedirect("index.jsp"); return;
        }
        
        String receiver = request.getParameter("receiver");
        String content = request.getParameter("content");
        
        MessageDAO.getInstance().sendMessage(admin.getJdi_user(), receiver, content);
        
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write("<script>alert('전송되었습니다.'); location.href='userList.do';</script>");
    }
}