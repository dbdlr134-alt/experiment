package com.mnu.blog.repository;

import com.mnu.blog.domain.BoardType;
import com.mnu.blog.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 1. 검색어만 있을 때 (기존 기능)
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    // 2. 카테고리별 보기 (검색어 없음) ★ 추가
    Page<Post> findByBoardType(BoardType boardType, Pageable pageable);

    // 3. 카테고리 안에서 검색하기 ★ 추가
    Page<Post> findByBoardTypeAndTitleContainingOrContentContaining(BoardType boardType, String title, String content, Pageable pageable);
}