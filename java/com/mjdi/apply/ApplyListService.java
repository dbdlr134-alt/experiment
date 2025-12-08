package com.mjdi.apply;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class ApplyListService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDTO admin = (UserDTO)request.getSession().getAttribute("sessionUser");
        if(admin == null || !"ADMIN".equals(admin.getJdi_role())) {
            response.sendRedirect("index.jsp"); return;
        }
        
        ArrayList<ApplyDTO> list = ApplyDAO.getInstance().getWaitList();
        request.setAttribute("list", list);
        request.getRequestDispatcher("admin/request_list.jsp").forward(request, response);
    }
}