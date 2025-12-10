package com.mjdi.qna;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;
public class AdminAnswerViewService implements Action {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. 관리자 체크
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("sessionUser");
        
        if (user == null || !"ADMIN".equals(user.getJdi_role())) {
            response.sendRedirect("index.jsp");
            return;
        }

        // 2. 파라미터(q_id) 받기
        String qIdStr = request.getParameter("q_id");
        if(qIdStr == null || qIdStr.equals("")) {
             response.sendRedirect("QnAController?cmd=admin_qna_list");
             return;
        }
        int qId = Integer.parseInt(qIdStr);

        // 3. DB 조회 (질문 내용 + 기존 답변들)
        QuestionDAO dao = QuestionDAO.getInstance();
        QuestionDTO qDto = dao.getQuestion(qId);
        List<QuestionDTO> aList = dao.getAnswerList(qId);
        
        // 4. JSP로 데이터 전달
        request.setAttribute("q", qDto);
        request.setAttribute("aList", aList);
        request.setAttribute("newLine", "\n"); // 줄바꿈 처리용
        
        // 5. 답변 작성 페이지로 이동 (이 파일도 admin 폴더에 있어야 해!)
        String path = "/admin/qna_answer.jsp"; 
        
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}