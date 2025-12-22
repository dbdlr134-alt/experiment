package com.mnu.jpstudy.repository;

import com.mnu.jpstudy.entity.Board;
import com.mnu.jpstudy.entity.ReplyThread;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReplyThreadRepository extends JpaRepository<ReplyThread, Long> {
    
    // 게시글의 최상위 댓글(부모가 null)만 조회
    // 대댓글은 Entity 설정(Cascade)에 의해 자동으로 딸려옵니다.
    List<ReplyThread> findByBoardAndParentIsNullOrderByCreatedAtAsc(Board board);
}