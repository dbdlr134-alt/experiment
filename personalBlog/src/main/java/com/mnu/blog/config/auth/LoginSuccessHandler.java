package com.mnu.blog.config.auth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        
        // 1. 로그인한 사용자 정보 가져오기
        PrincipalDetail principal = (PrincipalDetail) authentication.getPrincipal();

        // 2. 임시 비밀번호 사용자라면? -> 수정 페이지로 강제 이동!
        if (principal.getUser().isTempPw()) {
            getRedirectStrategy().sendRedirect(request, response, "/user/updateForm");
            return;
        }

        // 3. 아니라면? -> 원래 가려던 페이지(또는 홈)로 이동
        super.onAuthenticationSuccess(request, response, authentication);
    }
}