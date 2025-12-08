package com.mjdi.quiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.mjdi.user.PointDAO;
import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class WordQuizService implements Action {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();
        
        if ("POST".equalsIgnoreCase(method)) {
            processGrading(request, response);
        } else {
            String jlpt = request.getParameter("jlpt");
            if (jlpt == null || jlpt.isEmpty()) jlpt = "N5";

            List<QuizDTO> qlist = QuizDAO.getInstance().getRandomQuiz(jlpt);

            request.setAttribute("qlist", qlist);
            request.setAttribute("jlpt", jlpt);
            
            // ★ [수정 1] 경로 변경
            request.getRequestDispatcher("quiz/quiz.jsp").forward(request, response);
        }
    }

    private void processGrading(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("sessionUser");
        String userId = (user != null) ? user.getJdi_user() : null;

        int totalScore = 0;
        int correctCount = 0;
        int wrongCount = 0;
        int earnedPoints = 0;
        
        String isRetryParam = request.getParameter("isRetry");
        boolean isRetry = "true".equals(isRetryParam);
        
        List<Map<String, String>> resultList = new ArrayList<>();
        
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            if (paramName.startsWith("ans_")) {
                String quizIdStr = paramName.substring(4);
                int quizId = Integer.parseInt(quizIdStr);
                
                String myAns = request.getParameter(paramName);
                String correctAns = request.getParameter("correct_" + quizIdStr);
                String word = request.getParameter("word_" + quizIdStr);
                
                boolean isCorrect = myAns.equals(correctAns);
                
                Map<String, String> map = new HashMap<>();
                map.put("word", word);
                map.put("myAns", myAns);
                map.put("correctAns", correctAns);
                map.put("isCorrect", isCorrect ? "O" : "X");
                resultList.add(map);
                
                if (isCorrect) {
                    correctCount++;
                    totalScore += 20;
                    if (isRetry && userId != null) {
                        QuizDAO.getInstance().removeIncorrectNote(userId, quizId);
                    }
                } else {
                    wrongCount++;
                    if (userId != null) {
                        QuizDAO.getInstance().addIncorrectNote(userId, quizId);
                    }
                }
            }
        }
        
        if (userId != null && correctCount > 0) {
            earnedPoints = correctCount * 5;
            PointDAO.getInstance().addPoint(userId, earnedPoints, "퀴즈 정답 보상");
        }

        // ★ [추가] 푼 문제 개수(정답+오답)만큼 카운트 증가
        if (userId != null) {
            int totalAttempt = correctCount + wrongCount; // 이번에 푼 총 개수
            QuizDAO.getInstance().updateSolveCount(userId, totalAttempt);
        }

        request.setAttribute("score", totalScore);

        request.setAttribute("score", totalScore);
        request.setAttribute("correctCount", correctCount);
        request.setAttribute("wrongCount", wrongCount);
        request.setAttribute("earnedPoints", earnedPoints);
        request.setAttribute("resultList", resultList);
        
        String isDaily = request.getParameter("isDaily");
        if ("true".equals(isDaily) && userId != null) {
            javax.servlet.ServletContext app = request.getServletContext();
            java.util.Set<String> solvedUsers = (java.util.Set<String>) app.getAttribute("todaySolvedUsers");
            if (solvedUsers == null) {
                solvedUsers = new java.util.HashSet<>();
                app.setAttribute("todaySolvedUsers", solvedUsers);
            }
            solvedUsers.add(userId);
        }
        
        // ★ [수정 2] 경로 변경
        request.getRequestDispatcher("quiz/quiz.jsp").forward(request, response);
    }
}