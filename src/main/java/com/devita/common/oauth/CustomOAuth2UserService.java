package com.devita.common.oauth;

import com.devita.common.exception.ErrorCode;
import com.devita.domain.category.dto.CategoryReqDto;
import com.devita.domain.category.service.CategoryService;
import com.devita.domain.user.domain.AuthProvider;
import com.devita.domain.user.domain.User;
import com.devita.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final CategoryService categoryService;


    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"kakao".equals(registrationId)) {
            throw new OAuth2AuthenticationException(ErrorCode.INVALID_TOKEN.getMessage());
        }

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) properties.get("nickname");
        String profileImage = (String) properties.get("profile_image");

        if (email == null) {
            throw new OAuth2AuthenticationException(ErrorCode.TOKEN_NOT_FOUND.getMessage());
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User(email, nickname, AuthProvider.KAKAO, profileImage);
                    User savedUser = userRepository.save(newUser);

                    createDefaultCategories(savedUser.getId());

                    return savedUser;
                });

        user.updateNickname(nickname);
        userRepository.save(user);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                attributes,
                "id"
        );
    }

    private void createDefaultCategories(Long userId) {
        String[] defaultCategories = {"일반", "강제 미션", "자율 미션"};
        for (String categoryName : defaultCategories) {
            CategoryReqDto categoryReqDto = new CategoryReqDto();
            categoryReqDto.setName(categoryName);
            categoryService.createCategory(userId, categoryReqDto);
        }
    }
}