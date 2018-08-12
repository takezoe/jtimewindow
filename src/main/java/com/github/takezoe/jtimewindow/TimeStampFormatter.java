package com.github.takezoe.jtimewindow;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Locale;

import static java.time.temporal.ChronoField.*;

/**
 * Port of airframe-metrics
 */
public class TimeStampFormatter {

    public static DateTimeFormatter noSpaceTimestampFormat = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral('T')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .appendLiteral('.')
            .appendValue(MILLI_OF_SECOND, 3)
            .appendOffset("+HHMM", "Z")
            .toFormatter(Locale.US);

    public static DateTimeFormatter humanReadableTimestampFormatter =  new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral(' ')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .appendOffset("+HHMM", "Z")
            .toFormatter(Locale.US);

    public static String formatTimestamp(ZonedDateTime time)
    {
        return humanReadableTimestampFormatter.format(time);
    }

    public static String formatTimestamp(long timeMillis, ZoneOffset zone) {
        ZonedDateTime timestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), zone);
        return humanReadableTimestampFormatter.format(timestamp);
    }

    public static String formatTimestampWithNoSpace(long timeMillis) {
        ZonedDateTime timestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), Constants.SystemTimeZone);
        return noSpaceTimestampFormat.format(timestamp);
    }

}
