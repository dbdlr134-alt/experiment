package com.mnu.blog.config.auth;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.mnu.blog.config.auth.provider.GoogleUserInfo;
import com.mnu.blog.config.auth.provider.OAuth2UserInfo;
import com.mnu.blog.domain.Role;
import com.mnu.blog.domain.User;
import com.mnu.blog.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder; // SecurityConfig에서 Bean으로 등록되어 있어야 함

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        
        OAuth2User oauth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oauth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = null;
        
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            log.info("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
        } else {
            log.info("우리는 구글만 지원해요");
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String password = encoder.encode(UUID.randomUUID().toString());
        String nickname = oAuth2UserInfo.getName();
        
        // ★ [수정] 프로필 사진 처리 로직
        String profileUrl = (String) oauth2User.getAttributes().get("picture");
        
        // 사진이 없거나 빈값이면 -> 깔끔한 회색 기본 이미지로 설정
        if (profileUrl == null || profileUrl.isBlank()) {
            profileUrl = "https://dummyimage.com/100x100/ced4da/6c757d.jpg?text=User";
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        
        User user;
        if (userOptional.isEmpty()) {
            user = User.builder()
                    .username(username)
                    .password(password)
                    .nickname(nickname)
                    .role(Role.USER)
                    .provider(provider)
                    .providerId(providerId)
                    .profileUrl(profileUrl) // ★ 결정된 이미지 저장
                    .phoneNumber("소셜로그인") 
                    .build();
            userRepository.save(user);
        } else {
            user = userOptional.get();
            user.setProfileUrl(profileUrl); // 기존 회원도 사진 최신화
            userRepository.save(user);
        }

        return new PrincipalDetail(user, oauth2User.getAttributes());
    }
}