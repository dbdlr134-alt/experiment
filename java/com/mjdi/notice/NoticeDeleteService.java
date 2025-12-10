package com.mjdi.notice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class NoticeDeleteService implements Action {

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
	    
	    // 세션에서 사용자 정보 가져오기
	    UserDTO adminUser = (UserDTO) request.getSession().getAttribute("sessionUser");
	    
	    if(adminUser == null || !"ADMIN".equals(adminUser.getJdi_role())) {
	        response.sendRedirect(request.getContextPath() + "/index.jsp");
	        return; // 관리자 아니면 접근 차단
	    }

	    int idx = Integer.parseInt(request.getParameter("idx"));
	    NoticeDAO dao = NoticeDAO.getInstance();
	    dao.noticeDelete(idx); // DAO에서 실제 삭제 처리

	    response.sendRedirect(request.getContextPath() + "/NoticeController?cmd=notice_list");
	}

}
