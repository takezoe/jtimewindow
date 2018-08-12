package com.github.takezoe.jtimewindow;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Port of airframe-metrics
 */
public class TimeWindow {

    private ZonedDateTime start;
    private ZonedDateTime end;

    public TimeWindow(ZonedDateTime start, ZonedDateTime end) {
        this.start = start;
        this.end = end;
    }

    public ZonedDateTime getStart(){
        return this.start;
    }

    public ZonedDateTime getEnd(){
        return this.end;
    }

    private Instant instantOfStart(){
        return start.toInstant();
    }

    private Instant instantOfEnd(){
        return end.toInstant();
    }

    public long startUnixTime(){
        return start.toEpochSecond();
    }

    public long endUnixTime(){
        return end.toEpochSecond();
    }

    public long startEpochMillis(){
        return instantOfStart().toEpochMilli();
    }

    public long endEpochMillis(){
        return instantOfEnd().toEpochMilli();
    }

    @Override
    public String toString(){
        String s = TimeStampFormatter.formatTimestamp(start);
        String e = TimeStampFormatter.formatTimestamp(end);
        return "[" + s + "," + e + ")";
    }

    public String toStringAt(ZoneOffset zone){
        String s = TimeStampFormatter.formatTimestamp(startEpochMillis(), zone);
        String e = TimeStampFormatter.formatTimestamp(endEpochMillis(), zone);
        return "[" + s + "," + e + ")";
    }

    private List<TimeWindow> splitInfo(ChronoUnit unit){
        List<TimeWindow> b = new ArrayList<>();
        ZonedDateTime cursor = start;
        while(cursor.compareTo(end) < 0){
            ZonedDateTime e = null;
            switch(unit){
                case DAYS   : e = cursor.plus(1, unit).truncatedTo(unit); break;
                case HOURS  : e = cursor.plus(1, unit).truncatedTo(unit); break;
                case MINUTES: e = cursor.plus(1, unit).truncatedTo(unit); break;
                case WEEKS  : e = cursor.plus(1, unit).with(DayOfWeek.MONDAY); break;
                case MONTHS : e = cursor.plus(1, unit).withDayOfMonth(1); break;
                case YEARS  : e = cursor.plus(1, unit).withDayOfYear(1); break;
                default: throw new IllegalStateException("Invalid split unit " + unit + " for range " + toString());
            }
            if (e.compareTo(end) <= 0){
                b.add(new TimeWindow(cursor, e));
            } else {
                b.add(new TimeWindow(cursor, end));
            }
            cursor = e;
        }
        return b;
    }

    public List<TimeWindow> splitIntoHours(){
        return splitInfo(ChronoUnit.HOURS);
    }

    public List<TimeWindow> splitIntoDays(){
        return splitInfo(ChronoUnit.DAYS);
    }

    public List<TimeWindow> splitIntoMonths(){
        return splitInfo(ChronoUnit.MONTHS);
    }

    public List<TimeWindow> splitIntoWeeks(){
        return splitInfo(ChronoUnit.WEEKS);
    }

    public List<TimeWindow> splitAt(ZonedDateTime date) {
        if(date.compareTo(start) <= 0 || date.compareTo(end) > 0){
            return Arrays.asList(this);
        } else {
            return Arrays.asList(new TimeWindow(start, date), new TimeWindow(date, end));
        }
    }

    public TimeWindow plus(long n, ChronoUnit unit){
        return new TimeWindow(start.plus(n, unit), end.plus(n, unit));
    }

    public TimeWindow minus(long n, ChronoUnit unit){
        return plus(-1 * n, unit);
    }

    public boolean intersectsWith(TimeWindow other){
        return start.compareTo(other.end) < 0 && end.compareTo(other.start) > 0;
    }

    public static TimeWindowBuilder withTimeZone(String zoneName){
        Map<String, String> idMap = new HashMap<>(ZoneId.SHORT_IDS);
        idMap.put("PDT", "-07:00");
        idMap.put("EDT", "-04:00");
        idMap.put("CDT", "-05:00");
        idMap.put("MDT", "-06:00");

        ZoneId zoneId = ZoneId.of(idMap.getOrDefault(zoneName, zoneName));
        ZoneOffset offset = ZonedDateTime.now(zoneId).getOffset();

        return withTimeZone(offset);
    }

    public static TimeWindowBuilder withTimeZone(ZoneOffset zoneId){
        return new TimeWindowBuilder(zoneId);
    }

    public static TimeWindowBuilder withUTC(){
        return withTimeZone(Constants.UTC);
    }

    public static TimeWindowBuilder withSystemTimeZone(){
        return withTimeZone(Constants.SystemTimeZone);
    }

    public static ZonedDateTime truncateTo(ZonedDateTime t, ChronoUnit unit){
        switch(unit){
            case SECONDS: return t.truncatedTo(unit);
            case MINUTES: return t.truncatedTo(unit);
            case HOURS: return t.truncatedTo(unit);
            case DAYS: return t.truncatedTo(unit);
            case WEEKS: return t.truncatedTo(ChronoUnit.DAYS).with(DayOfWeek.MONDAY);
            case MONTHS: return t.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
            case YEARS: return t.withDayOfYear(1).truncatedTo(ChronoUnit.DAYS);
            default: throw new UnsupportedOperationException(unit + " is not supported");
        }
    }
}
