package com.example.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SchedulePeriod {
    DAY("매일"),
    WEEK("매주"),
    MONTH("매월"),
    YEAR("매년"),
    CUSTOM("마음대로");

    private final String desc;
}
