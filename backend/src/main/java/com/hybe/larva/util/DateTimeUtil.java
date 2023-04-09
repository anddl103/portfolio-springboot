package com.hybe.larva.util;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

public class DateTimeUtil {

    @Nullable
    public static Date toDate(@Nullable LocalDateTime source) {
        return Optional.ofNullable(source)
                .map(s -> Date.from(s.atZone(ZoneId.systemDefault()).toInstant()))
                .orElse(null);
    }

    @Nullable
    public static LocalDateTime fromDate(@Nullable Date source) {
        return Optional.ofNullable(source)
                .map(s -> LocalDateTime.ofInstant(s.toInstant(), ZoneId.systemDefault()))
                .orElse(null);
    }
}
