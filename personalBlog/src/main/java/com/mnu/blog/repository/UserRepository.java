package com.mnu.blog.repository;

import com.mnu.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 기존 아이디 찾기
    Optional<User> findByUsername(String username);
    
    // ★ [추가] 닉네임으로 회원 찾기
    Optional<User> findByNickname(String nickname);
}