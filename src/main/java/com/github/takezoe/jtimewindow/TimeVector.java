package com.github.takezoe.jtimewindow;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Port of airframe-metrics
 */
public class TimeVector {

    private final long duration;
    private final long offset;
    private final ChronoUnit unit;

    private TimeVector(long duration, long offset, ChronoUnit unit) {
        this.duration = duration;
        this.offset = offset;
        this.unit = unit;
    }

    private static final Pattern durationPattern = Pattern.compile("^(?<prefix>[+-]|last|next)?(?<num>[0-9]+)(?<unit>s|m|d|h|w|M|y)");

    public static TimeVector of(String s) {
        switch(s) {
            case "thisHour" : return new TimeVector(-1, 1, ChronoUnit.HOURS);
            case "today"    : return new TimeVector(-1, 1, ChronoUnit.DAYS);
            case "thisWeek" : return new TimeVector(-1, 1, ChronoUnit.WEEKS);
            case "thisMonth": return new TimeVector(-1, 1, ChronoUnit.MONTHS);
            case "thisYear" : return new TimeVector(-1, 1, ChronoUnit.YEARS);
            case "lastHour" : return new TimeVector(-1, 0, ChronoUnit.HOURS);
            case "yesterday": return new TimeVector(-1, 0, ChronoUnit.DAYS);
            case "lastWeek" : return new TimeVector(-1, 0, ChronoUnit.WEEKS);
            case "lastMonth": return new TimeVector(-1, 0, ChronoUnit.MONTHS);
            case "lastYear" : return new TimeVector(-1, 0, ChronoUnit.YEARS);
            case "nextHour" : return new TimeVector(1, 1, ChronoUnit.HOURS);
            case "tomorrow" : return new TimeVector(1, 1, ChronoUnit.DAYS);
            case "nextWeek" : return new TimeVector(1, 1, ChronoUnit.WEEKS);
            case "nextMonth": return new TimeVector(1, 1, ChronoUnit.MONTHS);
            case "nextYear" : return new TimeVector(1, 1, ChronoUnit.YEARS);
            default:
                Matcher m = durationPattern.matcher(s);
                if(m.find()){
                    long length = Integer.parseInt(m.group("num"));
                    ChronoUnit unit = unitOf(m.group("unit"));
                    String prefix = m.group("prefix");
                    if(prefix == null){
                        return new TimeVector(length, 0, unit);
                    }
                    switch(prefix){
                        case "-":
                            return new TimeVector(-1 * length, 0, unit);
                        case "last":
                            return new TimeVector(-1 * length, 0, unit);
                        case "+":
                            return new TimeVector(length, 0, unit);
                        case "next":
                            return new TimeVector(length, 0, unit);
                        default:
                            throw new IllegalArgumentException("Unknown duration prefix: " + prefix);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid duration: " + s);
                }
        }

    }

    private static ChronoUnit unitOf(String s) {
        switch(s) {
            case "s": return ChronoUnit.SECONDS;
            case "m": return ChronoUnit.MINUTES;
            case "d": return ChronoUnit.DAYS;
            case "h": return ChronoUnit.HOURS;
            case "w": return ChronoUnit.WEEKS;
            case "M": return ChronoUnit.MONTHS;
            case "y": return ChronoUnit.YEARS;
            default : throw new IllegalArgumentException("Unknown unit type " + s);
        }
    }

    public long getDuration(){
        return this.duration;
    }

    public long getOffset(){
        return this.offset;
    }

    public ChronoUnit getUnit(){
        return this.unit;
    }

    public TimeWindow timeWindowFrom(ZonedDateTime context) {
        ZonedDateTime grid = TimeWindow.truncateTo(context, unit);

        ZonedDateTime startOffset = grid.plus(offset, unit);
        ZonedDateTime end = startOffset.plus(duration, unit);

        boolean onGrid = grid.compareTo(context) == 0;
        ZonedDateTime start = null;
        if (onGrid) {
            start = startOffset;
        } else {
            start = context;
        }

        if (start.compareTo(end) <= 0) {
            return new TimeWindow(start, end);
        } else {
            return new TimeWindow(end, start);
        }
    }

}
