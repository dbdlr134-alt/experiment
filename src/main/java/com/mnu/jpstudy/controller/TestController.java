package com.mnu.jpstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mnu.jpstudy.entity.Dictionary;
import com.mnu.jpstudy.service.DictionaryCrawlerService;

@Controller
public class TestController {

    @Autowired
    private DictionaryCrawlerService crawlerService;

    // 테스트 주소: http://localhost:8080/test/crawl?text=猫
    @GetMapping("/test/crawl")
    @ResponseBody // 화면(HTML) 대신 데이터(글자)를 바로 보여줌
    public String testCrawl(@RequestParam("text") String text) {
        
    	Dictionary result = crawlerService.searchAndSave(text);

        if (result != null) {
            return "<h1>크롤링 성공!</h1>"
                  "<p>단어: "  result.getKanji()  "</p>"
                  "<p>읽기: "  result.getReading()  "</p>"
                  "<p>뜻: "  result.getMeanings()  "</p>";
        } else {
            return "<h1>검색 실패 ㅠㅠ</h1><p>단어를 찾을 수 없거나 크롤링 코드를 확인하세요.</p>";
        }
    }
}