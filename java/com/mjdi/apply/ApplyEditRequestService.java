package com.mjdi.apply;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.user.UserDTO;
import com.mjdi.util.Action;

public class ApplyEditRequestService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 로그인 체크
        UserDTO user = (UserDTO) request.getSession().getAttribute("sessionUser");
        if (user == null) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(
                "<script>alert('로그인이 필요합니다.'); location.href='login.jsp';</script>"
            );
            return;
        }

        // 2. word_id 파라미터 검증
        String wordIdParam = request.getParameter("word_id");
        if (wordIdParam == null || wordIdParam.trim().isEmpty()) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(
                "<script>alert('잘못된 접근입니다. 단어 상세 페이지에서 수정 신청을 해 주세요.'); history.back();</script>"
            );
            return;
        }

        int wordId;
        try {
            wordId = Integer.parseInt(wordIdParam.trim());
        } catch (NumberFormatException e) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(
                "<script>alert('잘못된 단어 번호입니다.'); history.back();</script>"
            );
            return;
        }

        // 3. DTO 구성
        ApplyDTO dto = new ApplyDTO();
        dto.setWordId(wordId);
        dto.setWord(request.getParameter("word"));
        dto.setDoc(request.getParameter("doc"));
        dto.setKorean(request.getParameter("korean"));
        dto.setJlpt(request.getParameter("jlpt"));
        dto.setJdiUser(user.getJdi_user());

        // 4. DB 저장
        ApplyDAO.getInstance().insertEditApply(dto);

        // 5. 완료 응답
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write(
            "<script>alert('수정 신청이 등록되었습니다'); location.href='WordController?cmd=main';</script>"
        );
    }
}
