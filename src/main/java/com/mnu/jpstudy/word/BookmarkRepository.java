package com.mnu.jpstudy.word;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserIdAndWord_WordId(String userId, Long wordId);

    boolean existsByUserIdAndWord_WordId(String userId, Long wordId);

    List<Bookmark> findAllByUserIdOrderByCreatedAtDesc(String userId);
}