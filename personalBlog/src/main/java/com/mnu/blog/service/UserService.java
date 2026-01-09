package com.mnu.blog.service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mnu.blog.domain.Role;
import com.mnu.blog.domain.User;
import com.mnu.blog.dto.UserJoinDto;
import com.mnu.blog.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder encoder;

    private DefaultMessageService messageService;
    
    // 인증번호 저장소
    private Map<String, String> verifyStore = new ConcurrentHashMap<>();

    // application.properties에서 값 가져오기
    @Value("${coolsms.api.key}") private String apiKey;
    @Value("${coolsms.api.secret}") private String apiSecret;
    @Value("${coolsms.api.number}") private String senderNumber;

    @PostConstruct
    public void init() {
        // 쿨에스엠에스 초기화
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    // 1. 중복 체크
    @Transactional(readOnly = true)
    public boolean checkUsernameDuplicate(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
    
    // 2. 인증번호 발송 (실제 발송)
    public void sendSms(String phoneNumber) {
        // 4자리 난수 생성
        String randomCode = String.format("%04d", new Random().nextInt(10000)); 
        
        Message message = new Message();
        message.setFrom(senderNumber); // 발신번호 (등록된 번호만 가능)
        message.setTo(phoneNumber.replaceAll("-", "")); // 수신번호 (하이픈 제거)
        message.setText("[MyBlog] 인증번호는 [" + randomCode + "] 입니다.");

        try {
            messageService.send(message); // 문자 전송
            verifyStore.put(phoneNumber, randomCode); // 메모리에 저장
            System.out.println("문자 전송 성공: " + randomCode); // 로그 확인용
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("문자 전송 실패: " + e.getMessage());
        }
    }

    // 3. 인증번호 확인
    public boolean verifySms(String phoneNumber, String code) {
        String savedCode = verifyStore.get(phoneNumber);
        return savedCode != null && savedCode.equals(code);
    }

    // 4. 회원가입
    @Transactional
    public void 회원가입(UserJoinDto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .password(encoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .phoneNumber(dto.getPhoneNumber())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        verifyStore.remove(dto.getPhoneNumber()); 
    }
    
 // ★ [추가] 닉네임 중복 체크 (true면 중복)
    @Transactional(readOnly = true)
    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    @Transactional
    public void 회원수정(User user) {
        User persistance = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원 찾기 실패"));

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String rawPassword = user.getPassword();
            String encPassword = encoder.encode(rawPassword);
            persistance.setPassword(encPassword);
        }

        persistance.setNickname(user.getNickname());
        persistance.setPhoneNumber(user.getPhoneNumber());
        
        // ★ [추가] 프로필 사진 변경
        if(user.getProfileUrl() != null) {
            persistance.setProfileUrl(user.getProfileUrl());
        }
    }
    
 // (관리자용) 전체 회원 목록 가져오기
    @Transactional(readOnly = true)
    public List<User> 회원목록() {
        return userRepository.findAll();
    }
    
    // (관리자용) 회원 강제 추방
    @Transactional
    public void 회원삭제(Long id) {
        userRepository.deleteById(id);
    }
    
    @Transactional
    // ★ 매개변수 변경: email -> phoneNumber
    public String 비밀번호찾기(String username, String phoneNumber) {
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // ★ [수정됨] 전화번호가 일치하는지 확인
        if (user.getPhoneNumber() == null || !user.getPhoneNumber().equals(phoneNumber)) {
             throw new IllegalArgumentException("등록된 전화번호와 일치하지 않습니다.");
        }

        // 소셜 로그인 유저 확인
        if (user.getProvider() != null && !user.getProvider().equals("empty")) { // empty체크는 null이 아닐때만
             // provider가 null이 아니면 소셜 로그인 유저
             throw new IllegalArgumentException("소셜 로그인 회원은 비밀번호 찾기가 불가능합니다.");
        }
        
        // (소셜 로그인 확인 로직을 좀 더 안전하게)
        if (user.getProvider() != null && !user.getProvider().isEmpty() && !user.getProvider().equals("null")) {
             throw new IllegalArgumentException("소셜 로그인 회원은 해당 플랫폼을 이용해주세요.");
        }

        // 임시 비밀번호 생성 (8자리)
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        // 비밀번호 변경
        String encPassword = encoder.encode(tempPassword);
        user.setPassword(encPassword);
        
        return tempPassword;
    }
    
    @Transactional(readOnly = true)
    public String 아이디찾기(String nickname) {
        // 1. 닉네임으로 회원 찾기
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임으로 가입된 회원이 없습니다."));

        // 2. 소셜 로그인 회원인지 확인 (아이디가 복잡하므로 알려줌)
        if (user.getProvider() != null && !user.getProvider().equals("empty") && !user.getProvider().equals("null")) {
            return "고객님은 [" + user.getProvider() + "] 소셜 로그인 회원입니다.";
        }

        // 3. 일반 회원이면 아이디 리턴
        return user.getUsername();
    }
}