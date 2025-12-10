package com.mjdi.qna;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class QnAViewService implements Action {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. 파라미터 받기
        String qIdStr = request.getParameter("q_id");
        
        if (qIdStr == null || qIdStr.equals("")) {
            response.sendRedirect("QnAController?cmd=qna_list");
            return;
        }

        int qId = Integer.parseInt(qIdStr);
        QuestionDAO dao = QuestionDAO.getInstance();

        // =======================================================
        // [쿠키를 이용한 조회수 중복 증가 방지 로직]
        // =======================================================
        Cookie[] cookies = request.getCookies();
        Cookie viewCookie = null;
        
        // 1) 기존에 'viewCookies'라는 쿠키가 있는지 확인
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("viewCookies")) {
                    viewCookie = c;
                    break;
                }
            }
        }
        
        // 2) 쿠키 상태에 따른 처리
        if (viewCookie == null) {
            // 쿠키가 아예 없음 -> 조회수 증가 + 새 쿠키 생성
            dao.increaseViewCount(qId);
            
            Cookie newCookie = new Cookie("viewCookies", "[" + qId + "]");
            newCookie.setPath("/"); 
            newCookie.setMaxAge(24 * 60 * 60); // 24시간 유지
            response.addCookie(newCookie);
            
        } else {
            // 쿠키가 있음 -> 현재 글(qId)을 본 적이 있는지 확인
            // 쿠키 값 예시: "[1][15][20]" 형태
            if (!viewCookie.getValue().contains("[" + qId + "]")) {
                // 본 적 없음 -> 조회수 증가 + 쿠키 값에 현재 글 ID 추가
                dao.increaseViewCount(qId);
                
                String newValue = viewCookie.getValue() + "[" + qId + "]";
                viewCookie.setValue(newValue);
                viewCookie.setPath("/");
                viewCookie.setMaxAge(24 * 60 * 60); // 시간 갱신
                response.addCookie(viewCookie);
            }
            // 이미 포함되어 있으면(본 적 있으면) 조회수 증가 안 함
        }
        // =======================================================

        
        // 3. 질문 상세 정보 가져오기 (이제 여기서는 조회수 증가 안 함, 순수 조회만)
        QuestionDTO qDto = dao.getQuestion(qId);
        
        // 4. 답변 리스트 가져오기
        List<QuestionDTO> aList = dao.getAnswerList(qId);
        
        // 5. 글이 없는 경우 예외 처리
        if (qDto == null) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('존재하지 않는 글입니다.');");
            out.println("location.href='QnAController?cmd=qna_list';");
            out.println("</script>");
            out.close();
            return;
        }

        // 6. JSP로 데이터 전송
        request.setAttribute("q", qDto);       // 닉네임 포함된 질문 정보
        request.setAttribute("aList", aList);  // 답변 목록
        request.setAttribute("newLine", "\n"); // 줄바꿈 처리를 위해

        String path = "/qna_view.jsp"; 
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}