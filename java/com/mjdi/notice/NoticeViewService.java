package com.mjdi.notice;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class NoticeViewService implements Action {

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int idx = Integer.parseInt(request.getParameter("idx"));
		
		NoticeDAO dao = NoticeDAO.getInstance();
		
		NoticeDTO dto = dao.noticeView(idx);
		dto.setContent(dto.getContent().replace("\n", "<br>"));
		
		request.setAttribute("dto", dto);
		
		RequestDispatcher rd = request.getRequestDispatcher("/notice/notice_view.jsp");
		rd.forward(request, response);

	}

}
