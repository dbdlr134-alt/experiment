package com.mjdi.user;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class ProfileReqListService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 관리자 권한 체크는 네 기존 방식에 맞게 추가 (role == admin 등)
        ProfileReqDAO dao = ProfileReqDAO.getInstance();
        List<ProfileReqDTO> list = dao.getPendingList();
        request.setAttribute("list", list);

        request.getRequestDispatcher("/admin/profile_request_list.jsp")
               .forward(request, response);
    }
}
