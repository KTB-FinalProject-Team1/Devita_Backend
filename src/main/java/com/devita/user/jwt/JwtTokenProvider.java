package com.devita.user.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.secret.access}")
    private String accessTokenSecret;

    @Value("${jwt.secret.refresh}")
    private String refreshTokenSecret;

    @Value("${jwt.expiration.access}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenValidityInMilliseconds;

    // 액세스 토큰 생성
    public String createAccessToken(Long userId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, accessTokenSecret)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(HttpServletResponse response, Long userId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, refreshTokenSecret)
                .compact();

        storeRefreshToken(response, userId, refreshToken);

        return refreshToken;
    }

    public String refreshAccessToken(String refreshToken, Long userId) {
        // Redis에서 리프레시 토큰 유효성 확인
        if (refreshTokenService.hasValidRefreshToken(userId, refreshToken)) {
            return createAccessToken(userId);
        }
        throw new IllegalArgumentException("Invalid refresh token");
    }

    public void storeRefreshToken(HttpServletResponse response, Long userId, String refreshToken){
        // Redis에 리프레시 토큰 저장
        refreshTokenService.saveRefreshToken(userId, refreshToken, refreshTokenValidityInMilliseconds);

        // 쿠키에 리프레시 토큰 저장
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) (refreshTokenValidityInMilliseconds / 1000));
        response.addCookie(refreshTokenCookie);

    }
    // 액세스 토큰 검증
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(accessTokenSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    // 토큰에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(accessTokenSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Long getUserIdFromRefreshToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(refreshTokenSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }
}
