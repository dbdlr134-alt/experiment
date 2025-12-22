package com.mnu.jpstudy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        // templates/index.html 파일을 찾아서 보여줌
        return "index";
    }
}