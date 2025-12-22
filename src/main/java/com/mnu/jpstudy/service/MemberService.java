package com.mnu.jpstudy.service;

import com.mnu.jpstudy.dto.MemberJoinDto;
import com.mnu.jpstudy.entity.Member;
import com.mnu.jpstudy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. 회원가입 로직
    public void join(MemberJoinDto dto) {
        // 중복 검사
        if (memberRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
        if (memberRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 회원 저장 (기본 권한: ROLE_USER)
        Member member = new Member(dto.getUsername(), encodedPassword, dto.getNickname(), "ROLE_USER");
        memberRepository.save(member);
    }

    // 2. 로그인 로직 (Spring Security가 자동으로 호출함)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // Security가 이해할 수 있는 User 객체로 변환하여 반환
        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword()) // DB에 저장된 암호화된 비밀번호
                .roles(member.getRole().replace("ROLE_", "")) // "USER" 형태로 넣어야 함
                .build();
    }
}