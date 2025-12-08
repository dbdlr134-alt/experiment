package com.mjdi.apply; // apply 패키지에 생성

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.user.UserDAO;
import com.mjdi.util.Action;
import com.mjdi.word.WordDAO;

public class AdminMainService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 필요한 DAO 인스턴스 획득
    	ApplyDAO applyDao = ApplyDAO.getInstance();
        WordDAO wordDao = WordDAO.getInstance();
        UserDAO userDao = UserDAO.getInstance();
        
        // 2. 통계 데이터 획득
        int totalUser = userDao.getUserCount();
        int newRequest = applyDao.getNewWaitCount(); // 신규 등록 대기
        int totalWord = wordDao.getTotalWordCount();
        int editRequest = applyDao.getEditWaitCount();
        int profileRequest = applyDao.getProfileWaitCount();
        int totalRequest = newRequest + editRequest + profileRequest;
        
        // 3. Request 객체에 저장
        request.setAttribute("totalUser", totalUser);
        request.setAttribute("totalRequest", totalRequest);
        request.setAttribute("totalWord", totalWord);
        
        // 4. Admin Dashboard JSP로 포워딩
        request.getRequestDispatcher("admin/main.jsp").forward(request, response);
    }
}