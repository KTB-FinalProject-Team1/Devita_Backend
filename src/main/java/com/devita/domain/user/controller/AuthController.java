package com.devita.domain.user.controller;

import com.devita.common.response.ApiResponse;
import com.devita.domain.user.domain.AuthDTO;
import com.devita.domain.user.domain.User;
import com.devita.domain.user.dto.UserAuthResponse;
import com.devita.domain.user.service.AuthService;
import com.devita.domain.user.service.KakaoUserInfoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final KakaoUserInfoService kakaoUserInfoService;


    @PostMapping("/user/info")
    public ApiResponse<UserAuthResponse> sendUserInitData(@RequestBody AuthDTO authDTO) {
        log.info("로그인 성공 후 유저 정보를 반환합니다.(액세스 토큰, 닉네임 ...)");

        Map<String, Object> userInfo = kakaoUserInfoService.getUserInfo(authDTO.getKakaoAccessToken());
        User user = authService.loadUser(userInfo);
        UserAuthResponse userAuthResponse = authService.issueAccessAndRefreshTokens(user.getId());

        log.info("userAuthResponse.refreshToken: " + userAuthResponse.refreshToken());
        log.info("userAuthResponse.accessToken: " + userAuthResponse.accessToken());

        return ApiResponse.success(userAuthResponse);
    }

    @PostMapping("/reissue")
//    public ResponseEntity<ApiResponse<String>> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
    public ResponseEntity<ApiResponse<String>> refreshAccessToken(@RequestHeader("Refresh") String refreshToken) {
        UserAuthResponse response = authService.refreshUserAuth(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + response.accessToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(response.accessToken()));
    }
}
