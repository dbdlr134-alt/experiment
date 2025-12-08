package com.mjdi.apply;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mjdi.user.PointDAO;
import com.mjdi.util.Action;
import com.mjdi.word.WordDAO;
import com.mjdi.word.WordDTO;

public class ApplyApproveService implements Action {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int reqId = Integer.parseInt(request.getParameter("id"));
        ApplyDAO dao = ApplyDAO.getInstance();
        ApplyDTO reqDto = dao.getApply(reqId);
        
        if(reqDto != null) {
            // 1. 단어장에 등록
            WordDTO wordDto = new WordDTO();
            wordDto.setWord(reqDto.getWord());
            wordDto.setDoc(reqDto.getDoc());
            wordDto.setKorean(reqDto.getKorean());
            wordDto.setJlpt(reqDto.getJlpt());
            
            WordDAO.getInstance().insertWord(wordDto);
            
            // 2. 상태 변경 및 포인트 지급
            dao.updateStatus(reqId, "OK");
            PointDAO.getInstance().addPoint(reqDto.getJdiUser(), 50, "단어등록 승인 보상");
        }
        response.sendRedirect("adminList.apply");
    }
}