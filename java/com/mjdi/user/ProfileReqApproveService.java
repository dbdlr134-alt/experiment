package com.mjdi.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class ProfileReqApproveService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        int reqId = Integer.parseInt(request.getParameter("id"));

        ProfileReqDAO reqDao = ProfileReqDAO.getInstance();
        UserDAO userDao = UserDAO.getInstance();
        PointDAO pointDao = PointDAO.getInstance();  // 포인트 차감용 (이미 있다면)

        ProfileReqDTO dto = reqDao.getRequest(reqId);
        if (dto == null) {
            response.getWriter().write("<script>alert('존재하지 않는 신청입니다.'); history.back();</script>");
            return;
        }

        String userId = dto.getUserId();
        String newProfilePath = dto.getImagePath();   // upload/profile/xxxx.png

        // 1. 유저 프로필 업데이트
        userDao.updateProfile(userId, newProfilePath);

        // 2. 포인트 차감 (예: 50 P)
        pointDao.addPoint(userId, -50, "프로필 이미지 변경 신청 승인");

        // 3. 신청 상태 변경
        reqDao.updateStatus(reqId, "APPROVED");

        response.getWriter().write(
            "<script>alert('프로필 변경을 승인했습니다.'); location.href='profileReqList.do';</script>"
        );
    }
}

