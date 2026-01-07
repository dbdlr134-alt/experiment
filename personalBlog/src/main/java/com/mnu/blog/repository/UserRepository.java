package com.mnu.blog.repository;

import com.mnu.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // SELECT * FROM user WHERE username = ?
    Optional<User> findByUsername(String username); 
}