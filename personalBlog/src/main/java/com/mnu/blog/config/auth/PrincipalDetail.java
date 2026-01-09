package com.mnu.blog.config.auth;

import com.mnu.blog.domain.User;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
// ★ 중요: UserDetails(일반 로그인)와 OAuth2User(소셜 로그인)를 둘 다 상속받아야 합니다.
public class PrincipalDetail implements UserDetails, OAuth2User {

    private User user;
    private Map<String, Object> attributes; // 소셜 로그인 정보를 담을 곳

    // 1. 일반 로그인용 생성자
    public PrincipalDetail(User user) {
        this.user = user;
    }

    // ★ 2. 소셜 로그인용 생성자 (이게 없어서 에러가 난 겁니다!)
    public PrincipalDetail(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // --- [OAuth2User 구현 메서드] ---
    @Override
    public Map<String, Object> getAttributes() {
        return attributes; // 소셜 정보 리턴
    }

    @Override
    public String getName() {
        return null; // 잘 안 써서 null 처리
    }

    // --- [UserDetails 구현 메서드 (기존)] ---
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
    // 권한 목록 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collectors = new ArrayList<>();
        collectors.add(() -> "ROLE_" + user.getRole());
        return collectors;
    }
}