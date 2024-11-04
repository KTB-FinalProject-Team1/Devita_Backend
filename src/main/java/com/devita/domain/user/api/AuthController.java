package com.devita.domain.user.api;

import com.devita.common.exception.ErrorCode;
import com.devita.common.exception.SecurityTokenException;
import com.devita.common.response.ApiResponse;
import com.devita.common.jwt.JwtTokenProvider;
import com.devita.domain.user.dto.UserAuthResponse;
import com.devita.domain.user.service.AuthService;
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

    private final AuthService authService;


    @PostMapping("/user/info")
    public ResponseEntity<ApiResponse<UserAuthResponse>> sendUserInitData(@CookieValue("refreshToken") String refreshToken) {
        UserAuthResponse response = authService.refreshUserAuth(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + response.getAccessToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(response));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<String>> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
        UserAuthResponse response = authService.refreshUserAuth(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + response.getAccessToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(response.getAccessToken()));
    }
}
