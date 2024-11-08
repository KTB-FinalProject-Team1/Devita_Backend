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
        String requestURI = request.getRequestURI();

        if (isRequest(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = resolveToken(request);

            // 토큰 유효성 검증
            if (!jwtTokenProvider.validateAccessToken(token)) {
                throw new SecurityTokenException(ErrorCode.INVALID_TOKEN);
            }

            String userId = jwtTokenProvider.getUserIdFromToken(token);
            List<GrantedAuthority> authorities = new ArrayList<>();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(Long.valueOf(userId), null, authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authentication set in SecurityContext for user: {}", userId);


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

    private boolean isRequest(String requestURI) {
        return requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/api-docs") ||
                requestURI.startsWith("/webjars") ||
                requestURI.startsWith("/api/v1/auth/user/info");
    }
}