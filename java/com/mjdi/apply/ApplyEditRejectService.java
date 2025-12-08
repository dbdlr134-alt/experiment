package com.mjdi.apply;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class ApplyEditRejectService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. ID 획득
        int reqId = Integer.parseInt(request.getParameter("id"));
        
        // 2. 상태를 'NO' (거절)로 변경
        ApplyDAO.getInstance().updateEditStatus(reqId, "NO"); 
        
        // 3. 수정 요청 목록으로 복귀
        response.sendRedirect("adminEditList.apply");
    }
}