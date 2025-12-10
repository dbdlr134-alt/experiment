package com.mjdi.notice;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class NoticeListService implements Action {

	@Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        NoticeDAO dao = NoticeDAO.getInstance();
        
        List<NoticeDTO> list = dao.noticeList();
        
        request.setAttribute("list", list);
        
        RequestDispatcher rd = request.getRequestDispatcher("/notice/notice_list.jsp");
		rd.forward(request, response);
    }
}