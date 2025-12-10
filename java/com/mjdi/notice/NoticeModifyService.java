package com.mjdi.notice;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class NoticeModifyService implements Action {

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	 int idx = Integer.parseInt(request.getParameter("idx"));
        NoticeDTO dto = NoticeDAO.getInstance().noticeView(idx);
        
        request.setAttribute("dto", dto);
        RequestDispatcher rd = request.getRequestDispatcher("/notice/notice_modify.jsp");
        rd.forward(request, response);
    }
}