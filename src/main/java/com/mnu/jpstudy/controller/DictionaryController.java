package com.mnu.jpstudy.controller;

import com.mnu.jpstudy.entity.JapaneseWord;
import com.mnu.jpstudy.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @GetMapping("/search")
    public String search(
            @RequestParam(value = "q", required = false) String keyword,
            @PageableDefault(size = 10, sort = "wordId", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // 1. 서비스 호출해서 검색 결과 가져오기
        Page<JapaneseWord> result = dictionaryService.searchDict(keyword, pageable);

        // 2. 화면(HTML)에 데이터 전달
        model.addAttribute("keyword", keyword);
        model.addAttribute("words", result);

        return "result"; // templates/result.html 로 이동
    }
}