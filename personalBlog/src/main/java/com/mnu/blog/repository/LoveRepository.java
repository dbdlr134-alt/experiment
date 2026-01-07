package com.mnu.blog.repository;

import com.mnu.blog.domain.Love;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LoveRepository extends JpaRepository<Love, Long> {
    // 유저가 해당 글에 좋아요를 눌렀는지 확인
    Optional<Love> findByUserIdAndPostId(Long userId, Long postId);
    
    // 좋아요 취소 (삭제)
    void deleteByUserIdAndPostId(Long userId, Long postId);
}