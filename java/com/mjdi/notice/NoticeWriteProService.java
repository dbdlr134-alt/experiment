package com.mjdi.notice;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class NoticeWriteProService implements Action {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // 1. 파라미터 받기
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        
        // 2. 세션에서 로그인한 관리자 정보 가져오기 (★ 이 부분이 핵심입니다)
        HttpSession session = request.getSession();
        UserDTO sessionUser = (UserDTO) session.getAttribute("sessionUser");
        
        // 로그인 안 되어 있거나 관리자가 아니면 튕겨내기 (안전장치)
        if(sessionUser == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        // 3. DTO 생성 및 데이터 담기
        NoticeDTO dto = new NoticeDTO();
        dto.setTitle(title);
        dto.setContent(content);
        
        // ★★★ 여기서 작성자 아이디(writer_id)를 꼭 넣어줘야 합니다! ★★★
        dto.setWriter_id(sessionUser.getJdi_user()); 

        // 4. DAO 호출
        NoticeDAO dao = NoticeDAO.getInstance();
        dao.noticeWrite(dto);
        
        // 5. 목록으로 이동
        response.sendRedirect("NoticeController?cmd=notice_list");
    }
}