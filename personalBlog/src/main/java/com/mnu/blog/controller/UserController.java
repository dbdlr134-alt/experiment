package com.mnu.blog.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mnu.blog.config.auth.PrincipalDetail;

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
    
 // 회원정보 수정 페이지 (마이페이지)
    @GetMapping("/user/updateForm")
    public String updateForm(@AuthenticationPrincipal PrincipalDetail principal, Model model) {
        model.addAttribute("principal", principal); // 현재 로그인한 정보 전달
        return "user/updateForm";
    }
    
 // 비밀번호 찾기 페이지 이동
    @GetMapping("/auth/findPassword")
    public String findPassword() {
        return "user/findPassword";
    }
    
 // 아이디 찾기 화면으로 이동
    @GetMapping("/auth/findId")
    public String findIdForm() {
        return "user/findId";
    }
}