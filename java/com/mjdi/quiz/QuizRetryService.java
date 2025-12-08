package com.mjdi.quiz;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class QuizRetryService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("sessionUser");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        QuizDAO dao = QuizDAO.getInstance();
        int count = dao.getIncorrectCount(user.getJdi_user());
        if (count < 10) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<script>alert('오답노트가 10개 이상이어야 복습할 수 있습니다! 현재: " + count + "개'); history.back();</script>");
            return;
        }
        
        List<QuizDTO> qlist = dao.getIncorrectQuiz(user.getJdi_user());
        request.setAttribute("qlist", qlist);
        request.setAttribute("jlpt", "오답 복습");
        request.setAttribute("isRetry", true);
        
        // ★ [수정] 경로 변경
        request.getRequestDispatcher("quiz/quiz.jsp").forward(request, response);
    }
}