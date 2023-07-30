package com.example.service.enums.exception.common;

import com.example.service.enums.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NOT_READ_HTTP_MESSAGE(HttpStatus.BAD_REQUEST, "잘못된 형식의 요청입니다."),
    NOT_FOUND(HttpStatus.BAD_REQUEST, "요청하는 데이터를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
