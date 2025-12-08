package com.mjdi.apply;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class ApplyRejectService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int rejectId = Integer.parseInt(request.getParameter("id"));
        ApplyDAO.getInstance().updateStatus(rejectId, "NO"); 
        response.sendRedirect("adminList.apply");
    }
}