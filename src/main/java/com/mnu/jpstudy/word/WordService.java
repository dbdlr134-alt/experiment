package com.mnu.jpstudy.word;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WordService {

    private final JapaneseWordRepository japaneseWordRepository;

    @Transactional(readOnly = true)
    public Page<JapaneseWord> searchWords(String keyword, Pageable pageable) {
        if (!StringUtils.hasText(keyword)) {
            return japaneseWordRepository.findAll(pageable);
        }
        return japaneseWordRepository.search(keyword.trim(), pageable);
    }

    @Transactional(readOnly = true)
    public List<JapaneseWord> autoComplete(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }
        String normalized = keyword.trim();
        return japaneseWordRepository
                .findTop10ByWordContainingIgnoreCaseOrKoreanContainingIgnoreCaseOrderByWordAsc(normalized, normalized);
    }

    @Transactional(readOnly = true)
    public JapaneseWord findById(Long id) {
        return japaneseWordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("단어가 존재하지 않습니다: " + id));
    }

    @Transactional
    public JapaneseWord createWord(String word, String doc, String korean, String jlpt) {
        JapaneseWord entity = JapaneseWord.builder()
                .word(word)
                .doc(doc)
                .korean(korean)
                .jlpt(jlpt)
                .build();

        return japaneseWordRepository.save(entity);
    }

    @Transactional
    public JapaneseWord updateWord(Long id, String word, String doc, String korean, String jlpt) {
        JapaneseWord entity = findById(id);
        entity.setWord(word);
        entity.setDoc(doc);
        entity.setKorean(korean);
        entity.setJlpt(jlpt);
        return japaneseWordRepository.save(entity);
    }
}