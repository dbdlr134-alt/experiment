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


public class AdminQnAListService implements Action {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. 관리자 보안 체크 (으헤~ 아무나 들어오면 안 되지)
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("sessionUser");
        
        if (user == null || !"ADMIN".equals(user.getJdi_role())) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // 2. DAO 호출해서 리스트 가져오기
        QuestionDAO dao = QuestionDAO.getInstance();
        List<QuestionDTO> list = dao.getQuestionList(); 
        
        // 3. JSP로 데이터 보내기
        request.setAttribute("qnaList", list); // JSP에서 items="${qnaList}"로 받음
        
        // 4. 네가 정한 파일 이름 'question_list.jsp'로 이동!
        // (경로는 WebContent/admin/ 폴더 안에 있다고 가정)
        String path = "/admin/Question_list.jsp"; 
        
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}