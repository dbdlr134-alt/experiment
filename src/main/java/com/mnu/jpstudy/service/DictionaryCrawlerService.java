package com.mnu.jpstudy.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mnu.jpstudy.entity.Dictionary;
import com.mnu.jpstudy.repository.DictionaryRepository;

import java.io.IOException;

@Service
public class DictionaryCrawlerService {

    @Autowired
    private DictionaryRepository dictionaryRepository; // DB 저장소 연결

    private final String BASE_URL = "https://dic.daum.net/search.do?dic=jp&q=";

public Dictionary searchAndSave(String keyword) {
        
        // 1. DB 확인
        if (dictionaryRepository.existsByKanji(keyword)) {
            System.out.println("✅ DB에서 가져옵니다: "  keyword);
            return dictionaryRepository.findByKanji(keyword);
        }

        System.out.println("🔍 크롤링 요청: "  keyword);

        try {
            String url = BASE_URL  keyword;
            Document doc = Jsoup.connect(url).timeout(5000).get();
            
            // 가장 첫 번째 단어 박스 찾기
            Element box = doc.selectFirst(".card_word");

            if (box == null) {
                System.out.println("❌ 단어 박스를 못 찾았습니다. (selector: .card_word)");
                return null;
            }
/*
            // [디버깅] HTML 구조를 콘솔에 찍어서 확인하기 (중요!)
            // 빨간색 텍스트로 나오지 않지만, 이클립스 콘솔에서 복사해서 볼 수 있습니다.
            System.out.println("=== [DEBUG] 가져온 HTML 구조 시작 ===");
            System.out.println(box.outerHtml()); 
            System.out.println("=== [DEBUG] 가져온 HTML 구조 끝 ===");

*/
         // ================= [최종 수정된 추출 로직] =================

            // 0. 가장 정확한 '주요 검색어' 구역(cleanword_type)을 먼저 찾습니다.
            // 이 구역 안에서만 찾아야 엄한 버튼이나 다른 단어가 안 섞입니다.
            Element cleanBox = box.selectFirst(".cleanword_type");
            
            if (cleanBox == null) {
                // 주요 단어 박스가 없으면 일반 검색 박스라도 찾기
                cleanBox = box; 
            }

            // 1. 읽는 법 (Reading) -> 클래스명: txt_cleansch
            String reading = "";
            Element readingEl = cleanBox.selectFirst(".txt_cleansch");
            if (readingEl != null) {
                reading = readingEl.text();
            }

            // 2. 표제어 (Kanji) -> 클래스명: sub_txt
            String kanji = "";
            Element kanjiEl = cleanBox.selectFirst(".sub_txt");
            if (kanjiEl != null) {
                kanji = kanjiEl.text(); // "学生"만 깔끔하게 가져옴
            } else {
                // 한자가 없는 경우(히라가나 단어), 읽는 법을 표제어로 씀
                kanji = reading;
            }

            // 3. 뜻 (Meanings) -> cleanBox 안에서만 리스트 찾기
            Elements meaningList = cleanBox.select(".list_search li .txt_search");
            StringBuilder meanings = new StringBuilder();
            
            for (Element m : meaningList) {
                // 뜻 안에 <daum:word> 같은 태그가 섞여있어서 text()로만 깔끔하게 추출
                meanings.append(m.text()).append(", ");
            }
            
            String finalMeanings = meanings.length() > 0 
                    ? meanings.substring(0, meanings.length() - 2) : "뜻 없음";

            // ========================================================

            // [결과 확인 로그]
            System.out.println("--------------------------------");
            System.out.println("🎯 최종 추출 결과 (정제됨)");
            System.out.println("1. 한자: "  kanji);
            System.out.println("2. 읽기: "  reading);
            System.out.println("3. 뜻: "  finalMeanings);
            System.out.println("--------------------------------");

            // kanji가 비어있으면 저장 안 함
            if (kanji.isEmpty()) return null;

            // 4. DB 저장
            Dictionary dict = new Dictionary();
            dict.setKanji(kanji);       // 学生
            dict.setReading(reading);   // がくせい
            dict.setMeanings(finalMeanings);
            dict.setExample("Daum 사전 크롤링");
            dict.setLevel("N/A");

            dictionaryRepository.save(dict); 
            System.out.println("💾 DB 저장 완료!");

            return dict;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}