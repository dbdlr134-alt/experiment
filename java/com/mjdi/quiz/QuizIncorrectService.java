package com.mjdi.quiz;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class QuizIncorrectService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("sessionUser");
        
        if(user == null) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<script>alert('로그인이 필요한 서비스입니다!'); location.href='login.jsp';</script>");
            return;
        }
        
        List<QuizDTO> noteList = QuizDAO.getInstance().getIncorrectNotes(user.getJdi_user());
        request.setAttribute("noteList", noteList);
        
        // ★ [수정] 경로 변경: quiz_incorrect_answer_note.jsp -> quiz/quiz_incorrect_answer_note.jsp
        request.getRequestDispatcher("quiz/quiz_incorrect_answer_note.jsp").forward(request, response);
    }
}