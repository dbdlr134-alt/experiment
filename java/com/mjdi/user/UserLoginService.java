package com.mjdi.user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mjdi.util.Action;

public class UserLoginService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String loginId = request.getParameter("id");
        String loginPw = request.getParameter("pw");
        String ctx = request.getContextPath();
        
        UserDAO dao = UserDAO.getInstance(); // getInstance 사용 권장
        UserDTO user = dao.loginCheck(loginId, loginPw);
        
        if(user != null) {
            // ★ [추가] 차단된 회원인지 확인
            if ("BLOCK".equals(user.getJdi_status())) {
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().write("<script>alert('관리자에 의해 차단된 계정입니다. 고객센터에 문의하세요.'); location.href='" + ctx + "/login.jsp';</script>");
                return;
            }

            // 정상 회원이면 세션 저장
            request.getSession().setAttribute("sessionUser", user);
            
            if("ADMIN".equals(user.getJdi_role())) {
                response.sendRedirect(ctx + "/adminMain.apply");
            } else {
                response.sendRedirect(ctx + "/index.jsp");
            }
        } else {
            response.sendRedirect(ctx + "/login.jsp?error=1");
        }
    }
}