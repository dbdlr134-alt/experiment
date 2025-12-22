package com.mnu.jpstudy.repository;

import com.mnu.jpstudy.entity.JapaneseWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JapaneseWordRepository extends JpaRepository<JapaneseWord, Long> {

    // 검색 쿼리 (한자 OR 읽는법 OR 뜻에 키워드가 포함되면 검색)
    @Query("SELECT w FROM JapaneseWord w WHERE " +
           "w.kanji LIKE %:keyword% OR " +
           "w.reading LIKE %:keyword% OR " +
           "w.meanings LIKE %:keyword%")
    Page<JapaneseWord> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 정확히 일치하는 단어 찾기 (크롤링 중복 방지용)
    boolean existsByKanjiAndReading(String kanji, String reading);
}