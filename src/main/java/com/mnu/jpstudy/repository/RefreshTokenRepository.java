package com.mnu.jpstudy.repository;

import com.mnu.jpstudy.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenValue(String tokenValue);
    void deleteByTokenValue(String tokenValue);
}