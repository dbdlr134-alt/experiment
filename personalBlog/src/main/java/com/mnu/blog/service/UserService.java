package com.mnu.blog.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mnu.blog.domain.Role;
import com.mnu.blog.domain.User;
import com.mnu.blog.dto.UserJoinDto;
import com.mnu.blog.dto.UserUpdateDto; // DTO 임포트
import com.mnu.blog.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로깅
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Slf4j // ★ 로그 사용
@Service
@RequiredArgsConstructor // final 필드 생성자 주입
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private DefaultMessageService messageService;

    // 인증번호 저장소
    private Map<String, String> verifyStore = new ConcurrentHashMap<>();

    // ★ application.properties에서 값 주입
    @Value("${coolsms.api.key}") private String apiKey;
    @Value("${coolsms.api.secret}") private String apiSecret;
    @Value("${coolsms.api.number}") private String senderNumber;
    @Value("${file.path}") private String uploadFolder; // ★ 파일 저장 경로 주입

    @PostConstruct
    public void init() {
        try {
            this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
        } catch (Exception e) {
            log.error("CoolSMS 초기화 실패: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public boolean checkUsernameDuplicate(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
    
    @Transactional(readOnly = true)
    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    public void sendSms(String phoneNumber) {
        String randomCode = String.format("%04d", new Random().nextInt(10000)); 
        
        Message message = new Message();
        message.setFrom(senderNumber);
        message.setTo(phoneNumber.replaceAll("-", ""));
        message.setText("[MyBlog] 인증번호는 [" + randomCode + "] 입니다.");

        try {
            messageService.send(message);
            verifyStore.put(phoneNumber, randomCode);
            log.info("문자 전송 성공 - 번호: {}, 코드: {}", phoneNumber, randomCode); // ★ 로그로 변경
        } catch (Exception e) {
            log.error("문자 전송 실패", e);
            throw new RuntimeException("문자 전송 실패");
        }
    }

    public boolean verifySms(String phoneNumber, String code) {
        String savedCode = verifyStore.get(phoneNumber);
        return savedCode != null && savedCode.equals(code);
    }

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

    // ★ DTO 적용 및 로직 개선
    @Transactional
    public void 회원수정(UserUpdateDto dto) {
        User persistence = userRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원 찾기 실패"));

     // 비밀번호 수정 로직
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            String encPassword = encoder.encode(dto.getPassword());
            persistence.setPassword(encPassword);
            persistence.setTempPw(false); // ★ [추가] 직접 비밀번호를 바꿨으니 깃발 끄기!
        }

        persistence.setNickname(dto.getNickname());
        persistence.setPhoneNumber(dto.getPhoneNumber());
    }

    // ★ [이동됨] 프로필 사진 변경 로직을 서비스로 이동
    @Transactional
    public String 프로필사진변경(Long userId, MultipartFile image) {
        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + image.getOriginalFilename();
        
        Path imageFilePath = Paths.get(uploadFolder + imageFileName);
        
        try {
            // 폴더가 없으면 생성
            File folder = new File(uploadFolder);
            if (!folder.exists()) folder.mkdirs();
            
            Files.write(imageFilePath, image.getBytes());
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("프로필 사진 업로드 실패");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 찾기 실패"));
        
        // DB에 저장할 경로 (WebMvcConfig에서 매핑 필요)
        String dbPath = "/images/profile/" + imageFileName;
        user.setProfileUrl(dbPath);
        
        return dbPath;
    }
    
    @Transactional(readOnly = true)
    public List<User> 회원목록() {
        return userRepository.findAll();
    }
    
    @Transactional
    public void 회원삭제(Long id) {
        userRepository.deleteById(id);
    }
    
    @Transactional
    public void 비밀번호찾기(String username, String phoneNumber) {
        // 1. 유저 확인
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (user.getPhoneNumber() == null || !user.getPhoneNumber().equals(phoneNumber)) {
             throw new IllegalArgumentException("등록된 전화번호와 일치하지 않습니다.");
        }

        // 소셜 로그인 유저인지 체크
        if (user.getProvider() != null && !user.getProvider().equals("empty")) {
             throw new IllegalArgumentException("소셜 로그인 회원은 해당 플랫폼을 이용해주세요.");
        }

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(encoder.encode(tempPassword));
        user.setTempPw(true); // ★ [추가] 임시 비밀번호 발급했으니 깃발 켜기!

        // 3. ★ [변경] 화면에 리턴하지 않고, 여기서 바로 문자로 전송
        Message message = new Message();
        message.setFrom(senderNumber); // application.properties의 발신번호
        message.setTo(phoneNumber.replaceAll("-", "")); // 수신번호 (하이픈 제거)
        message.setText("[MyBlog] 고객님의 임시 비밀번호는 [" + tempPassword + "] 입니다. 로그인 후 변경해주세요.");

        try {
            messageService.send(message);
            log.info("임시 비밀번호 문자 전송 성공: {}", phoneNumber);
        } catch (Exception e) {
            log.error("문자 전송 실패", e);
            throw new RuntimeException("문자 전송 시스템 오류가 발생했습니다.");
        }
    }
    @Transactional(readOnly = true)
    public String 아이디찾기(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임으로 가입된 회원이 없습니다."));

        if (user.getProvider() != null && !user.getProvider().equals("empty") && !user.getProvider().equals("null")) {
            return "고객님은 [" + user.getProvider() + "] 소셜 로그인 회원입니다.";
        }
        return user.getUsername();
    }
}