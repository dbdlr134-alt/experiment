package com.mjdi.word;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class BookmarkToggleService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDTO user = (UserDTO) request.getSession().getAttribute("sessionUser");
        if(user == null) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write("<script>alert('로그인이 필요합니다.'); location.href='login.jsp';</script>");
            return;
        }

        int wordId = Integer.parseInt(request.getParameter("word_id"));
        String userId = user.getJdi_user();
        BookmarkDAO dao = BookmarkDAO.getInstance();

        // 이미 있으면 삭제, 없으면 추가 (토글 로직)
        if(dao.isBookmarked(userId, wordId)) {
            dao.removeBookmark(userId, wordId);
        } else {
            dao.addBookmark(userId, wordId);
        }

        // 처리가 끝나면 다시 단어 상세 페이지로 돌아감 (새로고침 효과)
        response.sendRedirect("WordController?cmd=word_view&word_id=" + wordId);
    }
}