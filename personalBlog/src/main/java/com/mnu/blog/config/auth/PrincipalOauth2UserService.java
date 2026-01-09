package com.mnu.blog.config.auth;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.mnu.blog.domain.Role;
import com.mnu.blog.domain.User;
import com.mnu.blog.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 구글/카카오 등에서 유저 정보 가져오기
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        // 2. 구글인지 확인 및 정보 추출
        OAuth2UserInfo oAuth2UserInfo = null;
        
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
        } else {
            System.out.println("지원하지 않는 소셜 로그인입니다.");
        }

        // 3. 정보 추출
        String provider = oAuth2UserInfo.getProvider(); 
        String providerId = oAuth2UserInfo.getProviderId(); 
        String username = provider + "_" + providerId; 
        String password = encoder.encode(UUID.randomUUID().toString());
        String email = oAuth2UserInfo.getEmail();
        String nickname = oAuth2UserInfo.getName();
        
        // ★ [중요] 구글 프로필 사진 URL 가져오기
        String profileUrl = (String) oauth2User.getAttributes().get("picture");

        // 4. 가입 여부 확인 및 처리
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        User user;
        if (userOptional.isEmpty()) {
            // 신규 회원가입
            user = User.builder()
                    .username(username)
                    .password(password)
                    .nickname(nickname)
                    .role(Role.USER)
                    .provider(provider)
                    .providerId(providerId)
                    .profileUrl(profileUrl) // 최초 가입 시 사진 저장
                    .phoneNumber("소셜로그인") 
                    .build();
            userRepository.save(user);
        } else {
            // ★ [수정됨] 기존 회원일 경우 -> 프로필 사진과 이메일 최신화 (Update)
            user = userOptional.get();
            user.setProfileUrl(profileUrl); // 사진 업데이트
            userRepository.save(user);      // DB에 반영
        }

        return new PrincipalDetail(user, oauth2User.getAttributes());
    }
}

// 구글 정보 처리용 클래스 (파일 하단에 같이 두셔도 됩니다)
class GoogleUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}

// 인터페이스
interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}