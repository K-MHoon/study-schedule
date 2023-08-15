package com.example.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IsClear {
    Y("완료"), N("미완료");

    private final String desc;
}
