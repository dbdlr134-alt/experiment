package com.mjdi.qna;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class AdminAnswerWriteService implements Action {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // 1. 관리자 체크
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("sessionUser");
        
        if (user == null || !"ADMIN".equals(user.getJdi_role())) {
            response.sendRedirect("index.jsp");
            return;
        }

        // 2. 파라미터 받기
        String qIdStr = request.getParameter("q_id");
        String content = request.getParameter("content");
        String writerUser = user.getJdi_user(); // 세션에서 관리자 ID 가져옴
        
        // 3. DTO 포장
        QuestionDTO dto = new QuestionDTO();
        dto.setQ_id(Integer.parseInt(qIdStr));
        dto.setContent(content);
        dto.setWriter_user(writerUser);
        
        // 4. DB 저장 (DAO 호출)
        QuestionDAO dao = QuestionDAO.getInstance();
        int result = dao.insertAnswer(dto);
        
        // 5. 결과 처리
        if (result == 1) {
            // 성공하면 다시 '관리자 질문 목록'으로 이동
            response.sendRedirect("QnAController?cmd=admin_qna_list");
        } else {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('등록실패.');");
            out.println("history.back();");
            out.println("</script>");
            out.close();
        }
    }
}