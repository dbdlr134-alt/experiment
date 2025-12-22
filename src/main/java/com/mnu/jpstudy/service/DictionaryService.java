package com.mnu.jpstudy.service;

import com.mnu.jpstudy.entity.JapaneseWord;
import com.mnu.jpstudy.repository.JapaneseWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DictionaryService {

    private final JapaneseWordRepository wordRepository;
    private final DictionaryCrawlerService crawlerService; // 크롤러 추가 주입

    // 키워드로 단어 검색 (없으면 크롤링)
    public Page<JapaneseWord> searchDict(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }

        // 1. 먼저 DB에서 검색
        Page<JapaneseWord> dbResult = wordRepository.searchByKeyword(keyword, pageable);

        // 2. 결과가 있으면 바로 반환
        if (!dbResult.isEmpty()) {
            return dbResult;
        }

        // 3. DB에 없으면 크롤링 시도!
        System.out.println("DB에 데이터 없음. 크롤링 시작: " + keyword);
        List<JapaneseWord> crawledWords = crawlerService.crawlWords(keyword);

        // 4. 크롤링 된 단어들을 DB에 저장 (중복 방지 체크)
        for (JapaneseWord word : crawledWords) {
            // 똑같은 한자/뜻이 없을 때만 저장
            if (!wordRepository.existsByKanjiAndReading(word.getKanji(), word.getReading())) {
                 wordRepository.save(word);
            }
        }

        // 5. 방금 저장한 데이터를 포함해서 다시 DB 검색해서 반환
        // (Page 객체로 예쁘게 리턴하기 위해 다시 조회하는 것이 가장 깔끔함)
        if (!crawledWords.isEmpty()) {
            return wordRepository.searchByKeyword(keyword, pageable);
        }

        // 6. 진짜 없는 경우
        return Page.empty(pageable);
    }
}