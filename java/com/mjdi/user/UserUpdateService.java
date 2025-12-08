package com.mjdi.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.util.Action;

public class UserUpdateService implements Action {
    
    // 프로필 등록 신청 비용 (50 P)
    private static final int PROFILE_CHANGE_COST = 50; 

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. 파라미터 및 세션 정보 추출
        String uName = request.getParameter("name");
        String uPhone = request.getParameter("phone");
        String uEmail = request.getParameter("email");
        String uNewPw = request.getParameter("newPw");
        String uProfile = request.getParameter("profile"); // 새로 선택된 프로필 파일명
        
        UserDTO currentUser = (UserDTO)request.getSession().getAttribute("sessionUser");
        String userId = currentUser.getJdi_user();
        String oldProfile = currentUser.getJdi_profile(); // 기존 프로필 파일명

        UserDAO dao = new UserDAO();
        PointDAO pointDao = PointDAO.getInstance();
        
        // 2. [핵심 로직] 프로필 사진 변경 여부 및 포인트 확인
        boolean isProfileChanged = !oldProfile.equals(uProfile);
        boolean charged = true; // 포인트 차감 성공 여부 플래그
        
        if (isProfileChanged) {
            // 2-1. 포인트 잔액 확인
            if (!pointDao.checkPointSufficient(userId, PROFILE_CHANGE_COST)) {
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().write("<script>alert('새 프로필 등록 신청 비용 " + PROFILE_CHANGE_COST + " P가 부족합니다!'); location.href='mypage.jsp';</script>");
                return; // 포인트 부족으로 프로세스 중단
            }
            
            // 2-2. 포인트 차감 실행 (음수 값으로 기록)
            int logResult = pointDao.addPoint(userId, -PROFILE_CHANGE_COST, "새 프로필 등록 신청");
            
            if (logResult <= 0) {
                charged = false;
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().write("<script>alert('포인트 차감 중 DB 오류가 발생했습니다.'); history.back();</script>");
                return; // DB 오류로 프로세스 중단
            }
        }
        
        // 3. 회원 정보 업데이트 실행
        int updateRes = dao.updateAll(userId, uName, uPhone, uEmail, uNewPw, uProfile);
        
        // 4. 결과 처리
        if(updateRes > 0) {
            // 세션 정보 갱신
            currentUser.setJdi_name(uName);
            currentUser.setJdi_phone(uPhone);
            currentUser.setJdi_email(uEmail);
            currentUser.setJdi_profile(uProfile);
            
            request.getSession().setAttribute("sessionUser", currentUser);
            request.getSession().removeAttribute("isPwdChecked");
            
            response.setContentType("text/html; charset=UTF-8");
            
            // 프로필을 변경했다면 포인트 차감 메시지 추가
            String msg = charged && isProfileChanged ? PROFILE_CHANGE_COST + " P가 차감되었으며, 정보가 수정되었습니다." : "정보가 수정되었습니다.";
            
            response.getWriter().write("<script>alert('" + msg + "'); location.href='mypage.jsp';</script>");
        } else {
            response.sendRedirect("edit_profile.jsp?error=fail");
        }
    }
}