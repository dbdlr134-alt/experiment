package com.mjdi.qna;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;


public class QnAWriteProService implements Action {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. 한글 깨짐 방지 (가장 먼저 해야 함!)
        request.setCharacterEncoding("UTF-8");
        
        // 2. JSP(Form)에서 보낸 데이터 받기
        // questionWrite.jsp의 <input name="..."> 이름과 똑같아야 해
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String writerId = request.getParameter("writer_id"); // readonly로 박혀있던 그 아이디
        
        // 3. DTO 생성 및 데이터 포장
        QuestionDTO dto = new QuestionDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setWriter_user(writerId); // 네 DTO 변수명(writer_user)에 맞췄어
        
        // [참고] 만약 DB에 default 값이 없는 필드가 있다면 여기서 세팅해줘야 해
        // dto.setView_count(0); 
        
        // 4. DAO 호출 (DB 저장 요청)
        QuestionDAO dao = QuestionDAO.getInstance();
        int result = dao.insertQuestion(dto); // insert 성공 시 1, 실패 시 0 반환 가정
        
        // 5. 결과에 따른 페이지 이동 처리
        if (result == 1) {
            // [성공] 목록 페이지로 '리다이렉트' (새로고침 중복 등록 방지)
            // 주의: QnAServiceFactory에서 목록 보는 cmd가 'qna_list'였지?
            response.sendRedirect("QnAController?cmd=qna_list");
            
        } else {
            // [실패] 자바스크립트로 경고창 띄우고 뒤로 가기
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('등록 실패.');");
            out.println("history.back();");
            out.println("</script>");
            out.close();
        }
    }
}