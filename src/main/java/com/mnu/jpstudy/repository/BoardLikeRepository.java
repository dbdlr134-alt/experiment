package com.mnu.jpstudy.repository;
import com.mnu.jpstudy.entity.BoardLike;
import com.mnu.jpstudy.entity.Member;
import com.mnu.jpstudy.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    boolean existsByBoardAndMember(Board board, Member member);
    void deleteByBoardAndMember(Board board, Member member); // 좋아요 취소
}