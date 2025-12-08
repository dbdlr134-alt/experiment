package com.mjdi.word;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class BookmarkListService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDTO user = (UserDTO) request.getSession().getAttribute("sessionUser");
        if(user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        ArrayList<WordDTO> list = BookmarkDAO.getInstance().getMyBookmarks(user.getJdi_user());
        request.setAttribute("bookmarkList", list);
        
        // 목록 페이지로 이동
        request.getRequestDispatcher("word/bookmark_list.jsp").forward(request, response);
    }
}