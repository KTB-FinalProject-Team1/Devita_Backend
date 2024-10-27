package com.devita.user.controller;

import com.devita.common.exception.ErrorCode;
import com.devita.common.exception.SecurityTokenException;
import com.devita.common.response.ApiResponse;
import com.devita.user.jwt.JwtTokenProvider;
import com.devita.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;


    @GetMapping("/auth/login")
    public String loginPage() {
        return "login";
    }


    @GetMapping("/home")
    public String home(Model model) {
        String name = userService.getCurrentUserName();
        model.addAttribute("name", name);
        return "home";
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
            String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken, userId);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + newAccessToken);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(ApiResponse.success(newAccessToken)); // ApiResponse 형식의 성공 응답 반환
            } catch (Exception e) {
            log.error("token update failed: {}", e.getMessage());
            throw new SecurityTokenException(ErrorCode.INTERNAL_TOKEN_SERVER_ERROR);
        }
    }
}
