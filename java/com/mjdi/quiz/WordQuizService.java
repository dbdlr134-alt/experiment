package com.mjdi.quiz;

import java.io.IOException;
import java.sql.Connection;
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
import com.mjdi.util.DBM;

public class WordQuizService implements Action {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();
        
        if ("POST".equalsIgnoreCase(method)) {
            processGrading(request, response);
        } else {
            // GET 방식: 퀴즈 출제 (읽기 전용이라 트랜잭션 불필요)
            String jlpt = request.getParameter("jlpt");
            if (jlpt == null || jlpt.isEmpty()) jlpt = "N5";

            List<QuizDTO> qlist = QuizDAO.getInstance().getRandomQuiz(jlpt);

            request.setAttribute("qlist", qlist);
            request.setAttribute("jlpt", jlpt);
            
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
        
        // DAO 인스턴스 준비
        QuizDAO quizDao = QuizDAO.getInstance();
        PointDAO pointDao = PointDAO.getInstance();
        
        Connection conn = null;

        try {
            // 1. 트랜잭션 시작
            conn = DBM.getConnection();
            conn.setAutoCommit(false); // 자동 커밋 끄기

            Enumeration<String> params = request.getParameterNames();
            while (params.hasMoreElements()) {
                String paramName = params.nextElement();
                if (paramName.startsWith("ans_")) {
                    String quizIdStr = paramName.substring(4);
                    int quizId = Integer.parseInt(quizIdStr);
                    
                    String myAns = request.getParameter(paramName);
                    String correctAns = request.getParameter("correct_" + quizIdStr); // 보안상 취약할 수 있으니 추후 DB 조회 방식으로 변경 권장
                    String word = request.getParameter("word_" + quizIdStr);
                    
                    boolean isCorrect = (myAns != null && myAns.equals(correctAns));
                    
                    Map<String, String> map = new HashMap<>();
                    map.put("word", word);
                    map.put("myAns", myAns);
                    map.put("correctAns", correctAns);
                    map.put("isCorrect", isCorrect ? "O" : "X");
                    resultList.add(map);
                    
                    if (isCorrect) {
                        correctCount++;
                        totalScore += 20;
                        // 오답 복습 모드일 경우, 맞췄으면 오답노트에서 제거
                        if (isRetry && userId != null) {
                            // [QuizDAO 메서드 추가 필요]
                            quizDao.removeIncorrectNoteWithConn(conn, userId, quizId);
                        }
                    } else {
                        wrongCount++;
                        // 틀렸으면 오답노트에 추가
                        if (userId != null) {
                            // [QuizDAO 메서드 추가 필요]
                            quizDao.addIncorrectNoteWithConn(conn, userId, quizId);
                        }
                    }
                }
            }
            
            // 2. 포인트 지급 (Connection 전달)
            if (userId != null && correctCount > 0) {
                earnedPoints = correctCount * 5;
                // PointDAO.addPoint(Connection, ...) 사용
                pointDao.addPoint(conn, userId, earnedPoints, "퀴즈 정답 보상");
            }

            // 3. 문제 풀이 횟수 증가 (Connection 전달)
            if (userId != null) {
                int totalAttempt = correctCount + wrongCount;
                // [QuizDAO 메서드 추가 필요]
                quizDao.updateSolveCountWithConn(conn, userId, totalAttempt);
            }

            // 4. 모든 DB 작업 성공 시 커밋
            conn.commit();

        } catch (Exception e) {
            // 5. 실패 시 롤백
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) {}
            }
            e.printStackTrace();
            // 에러 시 사용자에게 알림을 줄지, 아니면 결과 화면은 보여주되 데이터 저장은 안 할지 결정 필요
            // 여기서는 결과 화면은 보여주되 데이터 저장은 실패한 상태로 진행
        } finally {
            // 6. 연결 종료
            DBM.close(conn, null, null);
        }

        // 결과 화면 데이터 세팅
        request.setAttribute("score", totalScore);
        request.setAttribute("correctCount", correctCount);
        request.setAttribute("wrongCount", wrongCount);
        request.setAttribute("earnedPoints", earnedPoints);
        request.setAttribute("resultList", resultList);
        
        // 일일 미션 처리 (세션/어플리케이션 스코프 메모리 작업이라 트랜잭션과 무관)
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
        
        request.getRequestDispatcher("quiz/quiz.jsp").forward(request, response);
    }
}