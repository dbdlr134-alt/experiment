package com.mnu.jpstudy.word;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JapaneseWordRepository extends JpaRepository<JapaneseWord, Long> {

    @Query("""
        SELECT w
        FROM JapaneseWord w
        WHERE LOWER(w.word) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(w.korean) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(w.jlpt) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY CASE WHEN LOWER(w.word) = LOWER(:keyword) THEN 0 ELSE 1 END, LENGTH(w.word)
    """)
    Page<JapaneseWord> search(@Param("keyword") String keyword, Pageable pageable);

    List<JapaneseWord> findTop10ByWordContainingIgnoreCaseOrKoreanContainingIgnoreCaseOrderByWordAsc(String word, String korean);

    boolean existsByWordIgnoreCase(String word);
}