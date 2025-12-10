package com.mjdi.notice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class NoticeTopService implements Action {

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	 int idx = Integer.parseInt(request.getParameter("idx"));
        boolean isTop = Boolean.parseBoolean(request.getParameter("isTop"));

        NoticeDAO dao = NoticeDAO.getInstance();
        dao.setTopNotice(idx, isTop);

        // 다시 상세보기나 리스트로 돌아가기
        response.sendRedirect(request.getContextPath() + "/NoticeController?cmd=notice_list");
    }
}