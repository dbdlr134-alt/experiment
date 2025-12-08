package com.mjdi.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class ProfileReqRejectService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        int reqId = Integer.parseInt(request.getParameter("id"));
        ProfileReqDAO reqDao = ProfileReqDAO.getInstance();

        reqDao.updateStatus(reqId, "REJECTED");

        response.getWriter().write(
            "<script>alert('프로필 변경 신청을 거절했습니다.'); location.href='profileReqList.do';</script>"
        );
    }
}
