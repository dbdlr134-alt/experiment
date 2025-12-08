package com.mjdi.apply;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class ApplyEditApproveService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        
        if(idStr != null) {
            int reqId = Integer.parseInt(idStr);
            ApplyDAO dao = ApplyDAO.getInstance();
            ApplyDTO dto = dao.getEditApply(reqId);
            
            if(dto != null) {
                dao.approveEditApply(dto); // 실제 DB 수정
            }
        }
        
        // 승인 후 알림창 띄우고 목록으로 복귀
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('수정 내역이 반영되었습니다.');");
        out.println("location.href='adminEditList.apply';");
        out.println("</script>");
    }
}