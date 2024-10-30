package com.devita.common.jwt;

import com.devita.common.exception.ErrorCode;
import com.devita.common.exception.SecurityTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);

            // 토큰이 없을 경우 예외
            if (token == null) {
                throw new SecurityTokenException(ErrorCode.TOKEN_NOT_FOUND);
            }

            // 토큰 유효성 검증
            if (!jwtTokenProvider.validateAccessToken(token)) {
                throw new SecurityTokenException(ErrorCode.INVALID_TOKEN);
            }

            // 토큰이 만료 검증
            if (jwtTokenProvider.validateAccessToken(token)) {
                throw new SecurityTokenException(ErrorCode.TOKEN_EXPIRED);
            }

            String userId = jwtTokenProvider.getUserIdFromToken(token);

            // 필요 시 권한 리스트 추가
            List<GrantedAuthority> authorities = new ArrayList<>();

            // 사용자 ID와 권한을 포함한 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId, null, authorities);

            // 요청의 세부 정보를 인증 객체에 설정
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // SecurityContext에 사용자 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Set Authentication to security context for '{}', uri: {}", authentication.getName(), request.getRequestURI());

        } catch (SecurityTokenException e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
            throw new SecurityTokenException(ErrorCode.INTERNAL_SECURITY_ERROR);
        } catch (Exception e) {
            log.error("Internal server error during authentication processing: {}", e.getMessage());
            throw new SecurityTokenException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 다음 필터로
        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 JWT 토큰을 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}