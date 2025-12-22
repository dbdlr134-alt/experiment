package com.mnu.jpstudy.service;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mnu.jpstudy.entity.Member;
import com.mnu.jpstudy.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public Member join(String username, String password, String nickname) {
        Member member = new Member();
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password)); // 암호화
        member.setNickname(nickname);
        member.setRole("ROLE_USER");
        return memberRepository.save(member);
    }

    // 시큐리티 로그인 처리 (UserDetailsService 구현)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRole().replace("ROLE_", "")) // "USER" or "ADMIN"
                .build();
    }
}