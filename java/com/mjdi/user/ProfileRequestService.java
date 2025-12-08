package com.mjdi.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.mjdi.util.Action;

public class ProfileRequestService implements Action {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        UserDTO user = (UserDTO) request.getSession().getAttribute("sessionUser");
        if (user == null) {
            response.getWriter().write("<script>alert('로그인이 필요합니다.'); location.href='login.jsp';</script>");
            return;
        }

        String userId = user.getJdi_user();

        // 1. 업로드 파일 받기
        Part filePart = request.getPart("profileImage");
        if (filePart == null || filePart.getSize() == 0) {
            response.getWriter().write("<script>alert('프로필 이미지를 선택해 주세요.'); history.back();</script>");
            return;
        }

        // 원본 파일명
        String submittedFileName = Paths.get(filePart.getSubmittedFileName())
                                        .getFileName().toString();

        // 확장자 추출
        String ext = "";
        int dot = submittedFileName.lastIndexOf(".");
        if (dot != -1) ext = submittedFileName.substring(dot);

        // 저장 파일명: profile_유저ID_타임스탬프.ext
        String saveFileName = "profile_" + userId + "_" + System.currentTimeMillis() + ext;

        // 저장 경로 (예: /upload/profile/)
        String uploadPath = request.getServletContext().getRealPath("/upload/profile");
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        // 실제 파일 저장
        filePart.write(uploadPath + File.separator + saveFileName);

        // DB에는 상대 경로만 저장 (예: upload/profile/파일명)
        String imagePath = "upload/profile/" + saveFileName;

        // 2. 신청 메모
        String comment = request.getParameter("comment");

        // 3. DB에 신청 기록 저장 (status = PENDING)
        ProfileReqDTO dto = new ProfileReqDTO();
        dto.setUserId(userId);
        dto.setImagePath(imagePath);
        dto.setComment(comment);
        dto.setStatus("PENDING");

        ProfileReqDAO dao = ProfileReqDAO.getInstance();
        dao.insertRequest(dto);

        // 4. 완료 안내
        response.getWriter().write(
            "<script>alert('새 프로필 이미지 신청이 접수되었습니다. 관리자 승인 후 적용됩니다.');"
          + "location.href='mypage.jsp';</script>"
        );
    }
}


