package com.github.takezoe.jtimewindow;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Port of airframe-metrics
 */
public class TimeParser {
    public static final DateTimeFormatter localDatePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter localDateTimePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS]");
    public static final List<DateTimeFormatter> zonedDateTimePatterns = Arrays.asList(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS][ z][XXXXX][XXXX]['['VV']']"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][ z][XXXXX][XXXX]['['VV']']")
    );

    public static ZonedDateTime parseLocalDateTime(String s, ZoneOffset zone) {
        try {
            LocalDateTime d = LocalDateTime.parse(s, localDateTimePattern);
            return ZonedDateTime.of(d, zone);
        } catch (Exception e1) {
            try {
                LocalDate d = LocalDate.parse(s, localDatePattern);
                return d.atStartOfDay(zone);
            } catch (Exception e2) {
                return null;
            }
        }
    }

    public static ZonedDateTime parseZonedDateTime(String s) {
        for(DateTimeFormatter formatter: zonedDateTimePatterns) {
            try {
                return ZonedDateTime.parse(s, formatter);
            } catch (Exception ex) {
            }
        }
        return null;
    }

    public static ZonedDateTime parseAtLocalTimeZone(String s)
    {
        return parse(s, Constants.SystemTimeZone);
    }

    public static ZonedDateTime parse(String s, ZoneOffset zone) {
        ZonedDateTime zonedDateTime = parseLocalDateTime(s, zone);
        if (zonedDateTime != null) {
            return zonedDateTime;
        }
        return parseZonedDateTime(s);
    }

}
