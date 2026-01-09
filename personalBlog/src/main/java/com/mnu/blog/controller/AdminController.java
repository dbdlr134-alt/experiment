package com.mnu.blog.controller;

import com.mnu.blog.service.BoardService;
import com.mnu.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final BoardService boardService;

    // 관리자 메인 대시보드
    @GetMapping("/admin")
    public String dashboard(Model model, 
                            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        
        // 1. 회원 목록 가져오기
        model.addAttribute("users", userService.회원목록());
        
        // 2. 전체 글 목록 가져오기 (기존 서비스 재활용)
        model.addAttribute("posts", boardService.글목록(pageable, null, null));
        
        return "admin/dashboard"; // admin 폴더 안의 dashboard.html
    }
}