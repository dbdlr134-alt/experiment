package com.mnu.jpstudy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mnu.jpstudy.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username); // 로그인용
    boolean existsByUsername(String username);

}
