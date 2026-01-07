package com.mnu.blog.service;

import java.util.Map;
import java.util.Random;
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
}