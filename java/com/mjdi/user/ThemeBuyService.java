package com.mjdi.user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mjdi.util.Action;

public class ThemeBuyService implements Action {
    // 가격은 DB에서 가져오는 것이 좋지만, 일단 파라미터나 고정값으로 처리
    // 여기서는 DB에서 가격을 가져오는 로직이 없으므로, 간단히 100P로 가정하거나
    // ThemeDTO를 활용하여 가격을 확인하는 로직을 추가할 수 있습니다.
    // 하지만 현재 구조상 파라미터로 가격을 넘겨받지 않으므로, 
    // DB의 jdi_theme_info 테이블에서 가격을 조회하거나,
    // 간단하게 모든 테마 가격을 100P로 통일하는 방법이 있습니다.
    // 여기서는 100P로 가정하고 진행합니다. 만약 테마별 가격이 다르다면 
    // request.getParameter("price") 등으로 가격을 받아오도록 수정해야 합니다.
    private static final int DEFAULT_PRICE = 100; 

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDTO user = (UserDTO) request.getSession().getAttribute("sessionUser");
        if (user == null) {
            response.sendRedirect("login.jsp"); return;
        }

        String themeCode = request.getParameter("theme");
        String userId = user.getJdi_user();
        PointDAO pDao = PointDAO.getInstance();
        UserDAO uDao = UserDAO.getInstance();

        // 1. 이미 보유중인지 확인
        if(uDao.getMyThemes(userId).contains(themeCode)) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write("<script>alert('이미 보유한 테마입니다.'); history.back();</script>");
            return;
        }

        // 2. 포인트 확인 및 차감
        // 실제로는 DB에서 해당 테마의 가격을 조회해야 정확합니다.
        // 여기서는 편의상 100P로 처리합니다. (추후 DB 연동 권장)
        if(!pDao.checkPointSufficient(userId, DEFAULT_PRICE)) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write("<script>alert('포인트가 부족합니다. (" + DEFAULT_PRICE + "P 필요)'); history.back();</script>");
            return;
        }
        
        pDao.addPoint(userId, -DEFAULT_PRICE, "테마 구매 (" + themeCode + ")");
        uDao.buyTheme(userId, themeCode);

        // 3. 구매 완료 후 적용 페이지로 이동
        response.setContentType("text/html; charset=UTF-8");
        // contextPath를 포함하여 정확한 경로로 이동
        String redirectUrl = request.getContextPath() + "/themeApply.do?theme=" + themeCode;
        response.getWriter().write("<script>alert('구매 완료! 테마를 적용합니다.'); location.href='" + redirectUrl + "';</script>");
    }
}