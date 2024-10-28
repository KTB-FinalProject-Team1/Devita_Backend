package com.devita.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class UserService {

    public String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
            // 닉네임을 OAuth2의 클레임에서 추출 (예: name)
            return (String) ((Map<String, Object>) oauth2User.getAttributes().get("properties")).get("nickname");
        }
        return "Guest";
    }
}