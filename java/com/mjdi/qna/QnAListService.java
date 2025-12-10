package com.mjdi.qna;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class QnAListService implements Action {

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		QuestionDAO dao= QuestionDAO.getInstance();
		
		// 3. DB에서 게시글 리스트 가져오기
        List<QuestionDTO> qnaList = dao.getQuestionList(); 

        // 4. request 영역에 데이터 저장 (이게 제일 중요해!)
        request.setAttribute("list", qnaList);

        // 5. 화면 이동 (포워딩)
		String path = "/qna_list.jsp"; 
		        
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
	}

}
