package com.mnu.jpstudy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnu.jpstudy.entity.JapaneseWord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryCrawlerService {

    // 네이버 검색 API (백과사전) URL
    private static final String NAVER_API_URL = "https://openapi.naver.com/v1/search/encyc.json";

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<JapaneseWord> crawlWords(String keyword) {
        List<JapaneseWord> wordList = new ArrayList<>();

        // 키가 없으면 실행하지 않음 (오류 방지)
        if (clientId == null || clientSecret == null) {
            log.warn("네이버 API 키가 설정되지 않았습니다.");
            return wordList;
        }

        try {
            // 1. 네이버 API 호출 주소 만들기
            URI uri = UriComponentsBuilder
                    .fromUriString(NAVER_API_URL)
                    .queryParam("query", keyword)
                    .queryParam("display", 5) // 5개만 가져오기
                    .queryParam("start", 1)
                    .encode(StandardCharsets.UTF_8)
                    .build()
                    .toUri();

            // 2. 요청 헤더에 ID와 Secret 추가
            RequestEntity<Void> req = RequestEntity
                    .get(uri)
                    .header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret)
                    .build();

            // 3. API 호출 및 응답 받기
            ResponseEntity<String> response = restTemplate.exchange(req, String.class);

            // 4. JSON 파싱
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.path("items");

            for (JsonNode item : items) {
                JapaneseWord word = new JapaneseWord();

                // 제목 (HTML 태그 제거) -> 한자/단어로 사용
                String title = removeHtmlTags(item.path("title").asText());
                
                // 설명 (HTML 태그 제거) -> 뜻으로 사용
                String description = removeHtmlTags(item.path("description").asText());

                // 네이버 백과사전 결과는 제목에 "단어 (한자)" 형식이 많음
                // 예: "私 (나 사)"
                word.setKanji(title);
                
                // API로는 요미가나(읽는법)가 따로 오지 않으므로, 일단 비워두거나 제목과 동일하게 설정
                word.setReading(""); 
                
                word.setMeanings(description);
                
                // 링크가 있다면 예문 칸에 잠시 저장 (나중에 원문 보러가기 용도)
                word.setExample("<a href='" + item.path("link").asText() + "' target='_blank'>네이버 백과 확인</a>");

                wordList.add(word);
            }

            log.info("네이버 API 검색 성공: '{}' ({}건)", keyword, wordList.size());

        } catch (Exception e) {
            log.error("네이버 API 호출 중 오류 발생: ", e);
        }

        return wordList;
    }

    // HTML 태그(<b>, </b> 등) 제거 헬퍼 메서드
    private String removeHtmlTags(String text) {
        if (text == null) return "";
        return text.replaceAll("<[^>]*>", ""); // 정규식으로 태그 삭제
    }
}