package com.mjdi.user;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mjdi.util.Action;

public class MessageBoxService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDTO user = (UserDTO)request.getSession().getAttribute("sessionUser");
        if(user == null) { response.sendRedirect("login.jsp"); return; }
        
        // 메세지 목록 가져오기
        ArrayList<MessageDTO> list = MessageDAO.getInstance().getMyMessages(user.getJdi_user());
        request.setAttribute("msgList", list);
        
        // 읽음 처리 (단순하게 목록만 봐도 다 읽은 것으로 처리하거나, 클릭 시 처리할 수 있음. 여기선 클릭 시 처리하도록 JSP에서 ajax 등을 쓰거나 별도 처리 필요하지만 간단하게 목록 조회)
        
        request.getRequestDispatcher("message_box.jsp").forward(request, response);
    }
}