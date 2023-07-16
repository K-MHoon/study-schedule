package com.example.studyschedule.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    public static String localDateTimeToString(LocalDateTime localDateTime) {
        if(Objects.isNull(localDateTime)) {
            return "";
        }

        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
