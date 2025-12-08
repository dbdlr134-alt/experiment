package com.mjdi.quiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class DailyQuizService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("sessionUser");
        
        // 로그인 유저 중복 체크
        if (user != null) {
            ServletContext app = request.getServletContext();
            Set<String> solvedUsers = (Set<String>) app.getAttribute("todaySolvedUsers");
            
            if (solvedUsers != null && solvedUsers.contains(user.getJdi_user())) {
                request.setAttribute("alreadySolved", true);
                request.setAttribute("msg", "오늘의 퀴즈는 하루에 한 번만 참여할 수 있어요!");
                request.setAttribute("savedScore", "참여 완료"); 
                // ★ [수정] 경로 변경: quiz.jsp -> quiz/quiz.jsp
                request.getRequestDispatcher("quiz/quiz.jsp").forward(request, response);
                return;
            }
        }

        // 퀴즈 가져오기
        ServletContext app = request.getServletContext();
        QuizDTO dailyQuiz = (QuizDTO) app.getAttribute("todayQuiz");
        List<QuizDTO> list = new ArrayList<>();
        
        if(dailyQuiz != null) {
            list.add(dailyQuiz);
        } else {
            QuizDAO.getInstance().checkAndSetGlobalQuiz(app);
            dailyQuiz = (QuizDTO) app.getAttribute("todayQuiz");
            if(dailyQuiz != null) list.add(dailyQuiz);
        }

        request.setAttribute("qlist", list);
        request.setAttribute("isDaily", true);
        request.setAttribute("jlpt", "오늘의 단어");

        // ★ [수정] 경로 변경
        request.getRequestDispatcher("quiz/quiz.jsp").forward(request, response);
    }
}