package com.mnu.jpstudy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // 메인 페이지 (인덱스)
    @GetMapping("/")
    public String index() {
        return "index"; // templates/index.html을 찾아감
    }
}