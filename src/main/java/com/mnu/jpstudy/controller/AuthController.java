package com.mnu.jpstudy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mnu.jpstudy.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // 로그인 페이지
    @GetMapping("/login")
    public String loginForm() {
        return "auth/login"; // templates/auth/login.html
    }

    // 회원가입 페이지
    @GetMapping("/join")
    public String joinForm() {
        return "auth/join";
    }

    // 회원가입 처리
    @PostMapping("/join")
    public String joinProcess(String username, String password, String nickname) {
        userService.join(username, password, nickname);
        return "redirect:/auth/login";
    }
}