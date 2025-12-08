package com.mjdi.word;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mjdi.apply.ApplyDAO;
import com.mjdi.apply.ApplyDTO;
import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class WordReqService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("sessionUser");

        if (user == null) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write("<script>alert('로그인이 필요한 서비스입니다.'); location.href='login.jsp';</script>");
            return;
        }

        ApplyDTO dto = new ApplyDTO();
        dto.setWord(request.getParameter("word"));
        dto.setDoc(request.getParameter("doc"));
        dto.setKorean(request.getParameter("korean"));
        dto.setJlpt(request.getParameter("jlpt"));
        dto.setJdiUser(user.getJdi_user());

        ApplyDAO dao = ApplyDAO.getInstance();
        int result = dao.insertApply(dto);

        response.setContentType("text/html; charset=UTF-8");
        if (result > 0) {
            response.getWriter().write("<script>alert('단어 신청이 완료되었습니다!\\n관리자 검토 후 등록됩니다.'); location.href='mypage.jsp';</script>");
        } else {
            response.getWriter().write("<script>alert('신청에 실패했습니다.'); history.back();</script>");
        }
    }
}