package com.mnu.jpstudy.word;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final JapaneseWordRepository japaneseWordRepository;

    private static final String DEFAULT_USER = "guest";

    private String resolveUser(String userId) {
        return StringUtils.hasText(userId) ? userId : DEFAULT_USER;
    }

    @Transactional
    public boolean toggleBookmark(String userId, Long wordId) {
        String owner = resolveUser(userId);
        return bookmarkRepository.findByUserIdAndWord_WordId(owner, wordId)
                .map(existing -> {
                    bookmarkRepository.delete(existing);
                    return false;
                })
                .orElseGet(() -> {
                    JapaneseWord word = japaneseWordRepository.findById(wordId)
                            .orElseThrow(() -> new IllegalArgumentException("단어가 존재하지 않습니다: " + wordId));
                    Bookmark bookmark = Bookmark.builder()
                            .userId(owner)
                            .word(word)
                            .build();
                    bookmarkRepository.save(bookmark);
                    return true;
                });
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(String userId, Long wordId) {
        String owner = resolveUser(userId);
        return bookmarkRepository.existsByUserIdAndWord_WordId(owner, wordId);
    }

    @Transactional(readOnly = true)
    public List<JapaneseWord> getBookmarks(String userId) {
        String owner = resolveUser(userId);
        return bookmarkRepository.findAllByUserIdOrderByCreatedAtDesc(owner).stream()
                .map(Bookmark::getWord)
                .collect(Collectors.toList());
    }
}