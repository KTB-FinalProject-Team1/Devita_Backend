package com.devita.user.controller;

import com.devita.common.exception.ErrorCode;
import com.devita.common.exception.SecurityTokenException;
import com.devita.common.response.ApiResponse;
import com.devita.user.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/access")
    public ResponseEntity<ApiResponse<String>> sendAccessToken(@CookieValue("refreshToken") String refreshToken) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
            String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken, userId);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + newAccessToken);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(ApiResponse.success(newAccessToken));
            } catch (Exception e) {
            log.error("token update failed: {}", e.getMessage());
            throw new SecurityTokenException(ErrorCode.INTERNAL_TOKEN_SERVER_ERROR);
        }
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
            String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken, userId);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + newAccessToken);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(ApiResponse.success(newAccessToken));
        } catch (Exception e) {
            log.error("token update failed: {}", e.getMessage());
            throw new SecurityTokenException(ErrorCode.INTERNAL_TOKEN_SERVER_ERROR);
        }
    }
}
