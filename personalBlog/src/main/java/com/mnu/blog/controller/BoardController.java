package com.mnu.blog.controller;

import com.mnu.blog.service.BoardService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 메인 페이지
    @GetMapping({"", "/"})
    public String index(Model model, 
                        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                        @RequestParam(value = "search", required = false) String search,
                        @RequestParam(value = "category", required = false) String category) {
        
        model.addAttribute("posts", boardService.글목록(pageable, search, category));
        model.addAttribute("search", search);
        model.addAttribute("category", category);
        return "index"; 
    }

    @GetMapping("/board/saveForm")
    public String saveForm() {
        return "board/saveForm";
    }

    // ★ 수정 포인트 1: @PathVariable("id") 추가
    @GetMapping("/board/{id}")
    public String findById(@PathVariable("id") Long id, Model model, 
                           HttpServletRequest request, 
                           HttpServletResponse response) {

        // 쿠키 로직 (기존 유지)
        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("viewedPosts")) {
                    oldCookie = cookie;
                }
            }
        }

        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + id + "]")) {
                boardService.조회수증가(id);
                oldCookie.setValue(oldCookie.getValue() + "_[" + id + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(oldCookie);
            }
        } else {
            boardService.조회수증가(id);
            Cookie newCookie = new Cookie("viewedPosts", "[" + id + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(newCookie);
        }
        
        model.addAttribute("post", boardService.글상세보기(id));
        return "board/detail";
    }

    // ★ 수정 포인트 2: @PathVariable("id") 추가
    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("post", boardService.글상세보기(id));
        return "board/updateForm";
    }
}