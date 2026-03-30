package io.dirmon.project.util;

import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeProvider {
    public static Instant convertDateToInstant(@NonNull Date date) {
        return Instant.ofEpochMilli(date.getTime());
    }

    public static Date convertInstantToDate(@NonNull Instant instant) {
        return Date.from(instant);
    }

    public static LocalDateTime convertDateToLocalDateTime(@NonNull Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static Date convertLocalDateTimeToDate(@NonNull LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Instant convertLocalDateTimeToInstant(@NonNull LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static LocalDateTime convertInstantToLocalDateTime(@NonNull Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
