// WordViewService.java 수정 (덮어쓰기 권장)
package com.mjdi.word;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mjdi.user.UserDTO; // 추가
import com.mjdi.util.Action;

public class WordViewService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("word_id");
        String query = request.getParameter("query");
        
        int word_id = 0;
        if(idStr != null && !idStr.isEmpty()) {
            word_id = Integer.parseInt(idStr);
        }
        
        WordDAO dao = WordDAO.getInstance();
        WordDTO vDto = dao.wordSelect(word_id);
        
        // ★ [추가] 로그인 상태라면 즐겨찾기 여부 확인
        UserDTO user = (UserDTO) request.getSession().getAttribute("sessionUser");
        boolean isBookmarked = false;
        if(user != null) {
            isBookmarked = BookmarkDAO.getInstance().isBookmarked(user.getJdi_user(), word_id);
        }
        request.setAttribute("isBookmarked", isBookmarked); // JSP로 전달

        request.setAttribute("vDto", vDto);
        request.setAttribute("searchQuery", query);
        
        request.getRequestDispatcher("word/word_view.jsp").forward(request, response);
    }
}