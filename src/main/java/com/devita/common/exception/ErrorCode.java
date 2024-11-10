package com.devita.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "잘못된 입력 값입니다."),

    // 403 Forbidden
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근이 거부되었습니다."),
    TODO_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "해당 사용자에게 할 일 접근 권한이 없습니다.."),
    CATEGORY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "해당 사용자에게 카테고리 접근 권한이 없습니다.."),

    // 404 Not Found
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "리소스를 찾을 수 없습니다."),
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "TODO_NOT_FOUND", "할 일을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "카테고리를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    VIEW_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "VIEW_TYPE_NOT_FOUND", "뷰타입은 weekly와 monthly만 받을 수 있습니다."),
    REWARD_NOT_FOUND(HttpStatus.NOT_FOUND, "REWARD_NOT_FOUND", "보상을 찾을 수 없습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 에러가 발생했습니다."),

    // Security 에러
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "SEC4001", "잘못된 형식의 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "SEC4011", "토큰이 만료되었습니다."),
    TOKEN_SIGNATURE_ERROR(HttpStatus.UNAUTHORIZED, "SEC4012", "토큰이 위조되었거나 손상되었습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "SEC4041", "토큰이 존재하지 않습니다."),
    INTERNAL_SECURITY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SEC5000", "인증 처리 중 서버 에러가 발생했습니다."),
    INTERNAL_TOKEN_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SEC5001", "토큰 처리 중 서버 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}