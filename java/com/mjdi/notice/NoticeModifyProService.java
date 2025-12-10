package com.mjdi.notice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class NoticeModifyProService implements Action {

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  request.setCharacterEncoding("UTF-8");
        
        int idx = Integer.parseInt(request.getParameter("idx"));
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        
        NoticeDTO dto = new NoticeDTO();
        dto.setIdx(idx);
        dto.setTitle(title);
        dto.setContent(content);
        
        NoticeDAO.getInstance().noticeUpdate(dto);
        
        // 수정 완료 후 상세보기 페이지로 이동
        response.sendRedirect(request.getContextPath() + "/NoticeController?cmd=notice_view&idx=" + idx);
    }
}
