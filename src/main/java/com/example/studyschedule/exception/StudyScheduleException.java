package com.example.studyschedule.exception;


import com.example.studyschedule.enums.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyScheduleException extends RuntimeException {

    private final ErrorCode errorCode;
}
