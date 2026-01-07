package com.mnu.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    // (주의) UserService 주입도 필요 없습니다. 여기서 안 쓰니까요.
    // (주의) @PostMapping("/auth/joinProc") 메서드는 지워야 합니다!
    // 그 기능은 UserApiController가 대신 합니다.

    // 회원가입 페이지 이동
    @GetMapping("/auth/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    // 로그인 페이지 이동
    @GetMapping("/auth/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }
}