package com.mnu.jpstudy.repository;

import com.mnu.jpstudy.entity.Member;
import com.mnu.jpstudy.entity.JapaneseWord;
import com.mnu.jpstudy.entity.MyWordbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyWordbookRepository extends JpaRepository<MyWordbook, Long> {
    
    // 특정 유저의 단어장 목록 조회 (최신순)
    Page<MyWordbook> findByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);

    // 이미 저장한 단어인지 체크
    boolean existsByMemberAndWord(Member member, JapaneseWord word);
}