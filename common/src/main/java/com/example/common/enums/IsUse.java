package com.example.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IsUse {
    Y("사용"), N("미사용");

    private final String desc;
}
