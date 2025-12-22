package com.mnu.jpstudy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mnu.jpstudy.entity.Member;
import com.mnu.jpstudy.entity.UserTheme;

public interface UserThemeRepository extends JpaRepository<UserTheme, Long> {
    List<UserTheme> findByMember(Member member);
    Optional<UserTheme> findByMemberAndIsEquippedTrue(Member member);
}