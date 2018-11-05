package com.github.takezoe.jtimewindow;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Port of airframe-metrics
 */
public class TimeWindowBuilder {

    private final ZoneOffset zone;
    private final ZonedDateTime currentTime;

    public TimeWindowBuilder(ZoneOffset zone){
        this(zone, null);
    }

    public TimeWindowBuilder(ZoneOffset zone, ZonedDateTime currentTime){
        this.zone = zone;
        this.currentTime = currentTime;
    }

    public ZoneOffset getZone(){
        return this.zone;
    }

    public ZonedDateTime getCurrentTime(){
        return this.currentTime;
    }

    public TimeWindowBuilder withOffset(ZonedDateTime t){
        return new TimeWindowBuilder(zone, t);
    }

    public TimeWindowBuilder withOffset(String dateTimeStr){
        ZonedDateTime d = TimeParser.parse(dateTimeStr, Constants.SystemTimeZone);
        if(d == null){
            throw new IllegalArgumentException("Invalid datetime: " + dateTimeStr);
        }
        return withOffset(d);
    }

    public TimeWindowBuilder withUnixTimeOffset(long unixTime){
        return withOffset(ZonedDateTime.ofInstant(Instant.ofEpochSecond(unixTime), Constants.UTC));
    }

    public ZonedDateTime now(){
        if(currentTime != null) {
            return currentTime;
        } else {
            return ZonedDateTime.now(zone);
        }
    }

    public ZonedDateTime beginningOfTheHour() {
        return now().truncatedTo(ChronoUnit.HOURS);
    }

    public ZonedDateTime endOfTheHour() {
        return beginningOfTheHour().plusHours(1);
    }

    public ZonedDateTime beginningOfTheDay() {
        return now().truncatedTo(ChronoUnit.DAYS);
    }

    public ZonedDateTime endOfTheDay() {
        return beginningOfTheDay().plusDays(1);
    }

    public ZonedDateTime beginningOfTheWeek() {
        return now().truncatedTo(ChronoUnit.DAYS).with(DayOfWeek.MONDAY);
    }

    public ZonedDateTime endOfTheWeek() {
        return beginningOfTheWeek().plusWeeks(1);
    }

    public ZonedDateTime beginningOfTheMonth() {
        return now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
    }

    public ZonedDateTime endOfTheMonth() {
        return beginningOfTheMonth().plusMonths(1);
    }

    public ZonedDateTime beginningOfTheYear() {
        return now().withDayOfYear(1).truncatedTo(ChronoUnit.DAYS);
    }

    public ZonedDateTime endOfTheYear() {
        return beginningOfTheYear().plusYears(1);
    }

    public TimeWindow today(){
        return new TimeWindow(beginningOfTheDay(), endOfTheDay());
    }

    public TimeWindow thisHour(){
        return new TimeWindow(beginningOfTheHour(), endOfTheHour());
    }

    public TimeWindow thisWeek(){
        return new TimeWindow(beginningOfTheWeek(), endOfTheWeek());
    }

    public TimeWindow thisMonth(){
        return new TimeWindow(beginningOfTheMonth(), endOfTheMonth());
    }

    public TimeWindow thisYear(){
        return new TimeWindow(beginningOfTheYear(), endOfTheYear());
    }

    public TimeWindow yesterday(){
        return today().minus(1, ChronoUnit.DAYS);
    }

    private ZonedDateTime parseOffset(String o, TimeWindowUnit windowUnit, List<TimeVector> adjustments){
        Pattern pattern = Pattern.compile("^(?<duration>[^/]+)(?<sep>/(?<offset>.+))");
        Matcher m = pattern.matcher(o);
        if(m.find()){
            adjustments.add(TimeVector.of(m.group("duration")));
            return parseOffset(m.group("offset"), windowUnit, adjustments);
        } else {
            if(o.equals("now")) {
                return adjustOffset(now(), adjustments);
            }
            try {
                // When the offset string is time duration patterns (e.g., 0M, 0d, etc.)
                TimeVector x = TimeVector.of(o);
                return x.timeWindowFrom(adjustOffset(now(), adjustments)).getStart();
            } catch (Exception ex){
                boolean truncate = true;
                if(o.endsWith(")")){
                    o = o.substring(0, o.length() - 1);
                    truncate = false;
                }

                ZonedDateTime d = TimeParser.parse(o, zone);
                if(d == null){
                    throw new IllegalArgumentException("Invalid offset string: " + o);
                }
                ZonedDateTime adjusted = adjustOffset(d, adjustments);
                if(!truncate){
                    return adjusted;
                }
                ZonedDateTime truncated = windowUnit.truncate(adjusted);
                if(truncated == null){
                    throw new IllegalArgumentException("Invalid offset string: " + o);
                }
                return truncated;
            }
        }
    }

    private ZonedDateTime adjustOffset(ZonedDateTime offset, List<TimeVector> adjustments){
        for(TimeVector duration: adjustments){
            offset = duration.getUnit().increment(offset, duration.getDuration());
        }
        return offset;
    }

    public TimeWindow parse(String str){
        Pattern pattern = Pattern.compile("^(?<duration>[^/]+)(?<sep>/(?<offset>.*))?");
        Matcher m = pattern.matcher(str);
        if(m.find()){
            String d = m.group("duration");
            TimeVector duration = TimeVector.of(d);
            String offsetStr = m.group("offset");
            if(offsetStr == null){
                ZonedDateTime context = duration.getUnit().truncate(now());
                return duration.timeWindowFrom(context);
            } else {
                ZonedDateTime offset = parseOffset(offsetStr, duration.getUnit(), new ArrayList<>());
                return duration.timeWindowFrom(offset);
            }
        } else {
            throw new IllegalArgumentException("TimeRange.of(" + str + ")");
        }
    }

    public TimeWindow fromRange(long startUnixTime, long endUnixTime){
        return new TimeWindow(Instant.ofEpochSecond(startUnixTime).atZone(zone), Instant.ofEpochSecond(endUnixTime).atZone(zone));
    }
}
