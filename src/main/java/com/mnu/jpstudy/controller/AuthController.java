package com.mnu.jpstudy.controller;

import com.mnu.jpstudy.dto.MemberJoinDto;
import com.mnu.jpstudy.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    // 로그인 페이지 보여주기
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login"; // templates/auth/login.html
    }

    // 회원가입 페이지 보여주기
    @GetMapping("/join")
    public String joinPage() {
        return "auth/join"; // templates/auth/join.html
    }

    // 회원가입 처리 (POST)
    @PostMapping("/join")
    public String joinProcess(MemberJoinDto dto) {
        try {
            memberService.join(dto);
            return "redirect:/auth/login"; // 성공하면 로그인 페이지로
        } catch (IllegalStateException e) {
            // 실패 시 다시 가입 페이지로 (에러 메시지 처리는 추후 추가)
            return "redirect:/auth/join?error=true";
        }
    }
}