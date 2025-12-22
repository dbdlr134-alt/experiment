package com.mnu.jpstudy.repository;

import com.mnu.jpstudy.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 최신순 조회
    Page<Board> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 검색 (제목 OR 내용)
    Page<Board> findByTitleContainingOrContentContainingOrderByCreatedAtDesc(
            String titleKeyword, String contentKeyword, Pageable pageable);
}