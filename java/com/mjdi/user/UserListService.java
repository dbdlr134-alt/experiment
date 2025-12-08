package com.mjdi.user;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class UserListService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDTO admin = (UserDTO)request.getSession().getAttribute("sessionUser");
        
        // 1. 관리자 권한 체크
        if(admin == null || !"ADMIN".equals(admin.getJdi_role())) {
            response.sendRedirect(request.getContextPath() + "/index.jsp"); 
            return;
        }

        // 2. 전체 회원 목록 조회
        UserDAO dao = UserDAO.getInstance();
        ArrayList<UserDTO> list = dao.getAllUsers();
        
        // 3. JSP에 데이터 전달 및 포워딩 (admin 폴더 내 user_list.jsp로 포워딩)
        request.setAttribute("userList", list);
        request.getRequestDispatcher("admin/user_list.jsp").forward(request, response);
    }
}