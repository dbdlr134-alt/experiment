package com.mnu.jpstudy.repository;

import com.mnu.jpstudy.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 로그인용 ID 조회
    Optional<Member> findByUsername(String username);

    // 닉네임 중복 체크용
    boolean existsByNickname(String nickname);
    
    // ID 중복 체크용
    boolean existsByUsername(String username);
}