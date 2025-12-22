package com.mnu.jpstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mnu.jpstudy.entity.Dictionary;
import com.mnu.jpstudy.service.DictionaryCrawlerService;

@Controller
public class DictionaryController {

    @Autowired
    private DictionaryCrawlerService crawlerService;

    // 1. 검색 입력 화면 (http://localhost:8080/search)
    @GetMapping("/search")
    public String showSearchForm() {
        // application.properties 설정에 따라 /WEB-INF/views/search.jsp를 찾음
        return "search"; 
    }

    // 2. 검색 실행 및 결과 처리 (http://localhost:8080/crawl?keyword=검색어)
    @GetMapping("/crawl")
    public String performCrawl(@RequestParam(name="keyword", required=false) String keyword, Model model) {
        
        // 1. 유효성 검사: 검색어가 텅 비어서 왔을 경우
        if (keyword == null || keyword.trim().isEmpty()) {
            return "redirect:/search"; // 다시 검색창으로 튕겨냄
        }

        System.out.println("🔎 컨트롤러 진입: "  keyword);

        // 2. 서비스 호출 (DB확인 -> 크롤링 -> 저장 -> 결과반환)
        Dictionary result = crawlerService.searchAndSave(keyword);

        // 3. 결과가 없을 경우 (크롤링 실패 등)
        if (result == null) {
            model.addAttribute("msg", "검색 결과를 찾을 수 없습니다.");
            return "search"; // 에러 메시지 들고 다시 검색창으로
        }

        // 4. 결과가 있으면 모델에 담아서 결과 페이지로 이동
        // JSP에서는 ${word.kanji}, ${word.meanings} 등으로 꺼내 쓸 수 있음
        model.addAttribute("word", result);
        
        return "result"; // /WEB-INF/views/result.jsp 로 이동
    }
}