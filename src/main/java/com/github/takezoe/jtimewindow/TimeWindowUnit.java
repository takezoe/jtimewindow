package com.github.takezoe.jtimewindow;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public enum TimeWindowUnit {

    Second("s") {
        @Override public ZonedDateTime truncate(ZonedDateTime t){
            return t.truncatedTo(ChronoUnit.SECONDS);
        }
        @Override
        public ZonedDateTime increment(ZonedDateTime a, long v) {
            return a.plus(v, ChronoUnit.SECONDS);
        }
    },
    Minute("m") {
        @Override public ZonedDateTime truncate(ZonedDateTime t){
            return t.truncatedTo(ChronoUnit.MINUTES);
        }
        @Override
        public ZonedDateTime increment(ZonedDateTime a, long v) {
            return a.plus(v, ChronoUnit.MINUTES);
        }
    },
    Hour("h") {
        @Override public ZonedDateTime truncate(ZonedDateTime t){
            return t.truncatedTo(ChronoUnit.HOURS);
        }
        @Override
        public ZonedDateTime increment(ZonedDateTime a, long v) {
            return a.plus(v, ChronoUnit.HOURS);
        }
    },
    Day("d") {
        @Override public ZonedDateTime truncate(ZonedDateTime t){
            return t.truncatedTo(ChronoUnit.DAYS);
        }
        @Override
        public ZonedDateTime increment(ZonedDateTime a, long v) {
            return a.plus(v, ChronoUnit.DAYS);
        }
    },
    Week("w") {
        @Override public ZonedDateTime truncate(ZonedDateTime t){
            return t.truncatedTo(ChronoUnit.DAYS).with(DayOfWeek.MONDAY);
        }
        @Override
        public ZonedDateTime increment(ZonedDateTime a, long v) {
            return a.plus(v, ChronoUnit.WEEKS);
        }
    },
    Month("M") {
        @Override public ZonedDateTime truncate(ZonedDateTime t){
            return t.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        }
        @Override
        public ZonedDateTime increment(ZonedDateTime a, long v) {
            return a.plus(v, ChronoUnit.MONTHS);
        }
    },
    Quarter("q") {
        @Override public ZonedDateTime truncate(ZonedDateTime t){
            int quarter = ((t.getMonthValue() - 1) / 3);
            int month = 3 * quarter;
            return t.withDayOfYear(1).plusMonths(month).truncatedTo(ChronoUnit.DAYS);
        }
        @Override
        public ZonedDateTime increment(ZonedDateTime a, long v) {
            int currentMonth = a.getMonthValue();
            long quarter = ((a.getMonthValue() - 1) / 3) + v;
            long targetMonth = (3 * quarter) + 1;
            return a.plus(targetMonth - currentMonth, ChronoUnit.MONTHS);
        }
    },
    Year("y") {
        @Override public ZonedDateTime truncate(ZonedDateTime t){
            return t.withDayOfYear(1).truncatedTo(ChronoUnit.DAYS);
        }
        @Override
        public ZonedDateTime increment(ZonedDateTime a, long v) {
            return a.plus(v, ChronoUnit.YEARS);
        }
    };

    private String symbol;
    TimeWindowUnit(String symbol){
        this.symbol = symbol;
    }
    public abstract ZonedDateTime truncate(ZonedDateTime t);
    public abstract ZonedDateTime increment(ZonedDateTime a, long v);

    public static TimeWindowUnit of(String symbol){
        for(TimeWindowUnit unit: values()){
            if(unit.symbol.equals(symbol)){
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown unit type " + symbol);
    }

}
