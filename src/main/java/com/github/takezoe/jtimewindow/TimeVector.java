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
    private final TimeWindowUnit unit;

    private TimeVector(long duration, long offset, TimeWindowUnit unit) {
        this.duration = duration;
        this.offset = offset;
        this.unit = unit;
    }

    private static final Pattern durationPattern = Pattern.compile("^(?<prefix>[+-]|last|next)?(?<num>[0-9]+)(?<unit>s|m|d|h|w|M|q|y)");

    public static TimeVector of(String s) {
        switch(s) {
            case "thisHour" : return new TimeVector(-1, 1, TimeWindowUnit.Hour);
            case "today"    : return new TimeVector(-1, 1, TimeWindowUnit.Day);
            case "thisWeek" : return new TimeVector(-1, 1, TimeWindowUnit.Week);
            case "thisMonth": return new TimeVector(-1, 1, TimeWindowUnit.Month);
            case "thisYear" : return new TimeVector(-1, 1, TimeWindowUnit.Year);
            case "lastHour" : return new TimeVector(-1, 0, TimeWindowUnit.Hour);
            case "yesterday": return new TimeVector(-1, 0, TimeWindowUnit.Day);
            case "lastWeek" : return new TimeVector(-1, 0, TimeWindowUnit.Week);
            case "lastMonth": return new TimeVector(-1, 0, TimeWindowUnit.Month);
            case "lastYear" : return new TimeVector(-1, 0, TimeWindowUnit.Year);
            case "nextHour" : return new TimeVector(1, 1, TimeWindowUnit.Hour);
            case "tomorrow" : return new TimeVector(1, 1, TimeWindowUnit.Day);
            case "nextWeek" : return new TimeVector(1, 1, TimeWindowUnit.Week);
            case "nextMonth": return new TimeVector(1, 1, TimeWindowUnit.Month);
            case "nextYear" : return new TimeVector(1, 1, TimeWindowUnit.Year);
            default:
                Matcher m = durationPattern.matcher(s);
                if(m.find()){
                    long length = Integer.parseInt(m.group("num"));
                    TimeWindowUnit unit = TimeWindowUnit.of(m.group("unit"));
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

    public long getDuration(){
        return this.duration;
    }

    public long getOffset(){
        return this.offset;
    }

    public TimeWindowUnit getUnit(){
        return this.unit;
    }

    public TimeWindow timeWindowFrom(ZonedDateTime context) {
        ZonedDateTime grid = unit.truncate(context);

        ZonedDateTime startOffset = unit.increment(grid, offset);
        ZonedDateTime end         = unit.increment(startOffset, duration);

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
