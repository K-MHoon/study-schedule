package com.example.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleType {

    PATTERN("일정 주기로 달성하는 방식"),
    LONG_TERM("일정 기간 동안 달성하는 방식"),
    NONE("해당하지 않음");

    private final String desc;
}
