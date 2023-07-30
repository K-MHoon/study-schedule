package com.example.service.exception;


import com.example.service.enums.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyScheduleException extends RuntimeException {

    private final ErrorCode errorCode;
}
