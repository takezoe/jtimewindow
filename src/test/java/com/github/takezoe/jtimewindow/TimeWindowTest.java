package com.github.takezoe.jtimewindow;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class TimeWindowTest {

    TimeWindowBuilder t = TimeWindow.withTimeZone("PDT").withOffset("2016-06-26 01:23:45-0700");
    ZoneOffset zone = t.getZone();

    TimeZone defaultTimeZone = TimeZone.getDefault();

    @Before
    public void beforeAll() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @After
    public void afterAll() {
        TimeZone.setDefault(defaultTimeZone);
    }

    public TimeWindow parse(String s, String expected) {
        TimeWindow w  = t.parse(s);
        String ws = w.toString(); // Check toString
        System.out.println("str:" + s + ", window: " + ws);
        assertEquals(expected, w.toStringAt(zone));
        return w;
    }

    @Test
    public void parseStringRepl(){
        // duration/offset

        // DURATION := (+ | -)?(INTEGER)(UNIT)
        // UNIT     := s | m | h | d | w | M | y
        //
        // OFFSET   := DURATION | DATE_TIME
        // RANGE    := (DURATION) (/ (OFFSET))?
        // DATE_TIME := yyyy-MM-dd( HH:mm:ss(.ZZZ| ' ' z)?)?
        //

        // The following tests are the results if the current time is 2016-06-26 01:23:45-0700

        // The default offset is 0(UNIT) (the beginning of the given time unit)

        // 0 means no duration from the beginning of specified time unit
        parse("0h", "[2016-06-26 01:00:00-0700,2016-06-26 01:00:00-0700)");
        parse("0d", "[2016-06-26 00:00:00-0700,2016-06-26 00:00:00-0700)");
        parse("0M", "[2016-06-01 00:00:00-0700,2016-06-01 00:00:00-0700)");

        // 1 hour from the beginning of today
        parse("1h", "[2016-06-26 01:00:00-0700,2016-06-26 02:00:00-0700)");
        // today
        parse("1d", "[2016-06-26 00:00:00-0700,2016-06-27 00:00:00-0700)");
        // this month
        parse("1M", "[2016-06-01 00:00:00-0700,2016-07-01 00:00:00-0700)");

        // 7 days ago until at the beginning of today.
        // 0d := the beginning of the day
        // [-7d, 0d)
        // |-------------|
        // -7d -- ... -- 0d ---- now  ------
        parse("-7d", "[2016-06-19 00:00:00-0700,2016-06-26 00:00:00-0700)");

        // Since 7 days ago + time fragment from [-7d, now)
        //  |-------------------|
        // -7d - ... - 0d ---- now  ------
        parse("-7d/now", "[2016-06-19 00:00:00-0700,2016-06-26 01:23:45-0700)");

        // '+' indicates forward time range
        // +7d = [0d, +7d)
        //      |------------------------------|
        // ---  0d --- now --- 1d ---  ... --- 7da
        parse("+7d", "[2016-06-26 00:00:00-0700,2016-07-03 00:00:00-0700)");
        // We can omit '+' sign
        parse("7d", "[2016-06-26 00:00:00-0700,2016-07-03 00:00:00-0700)");

        // [now, +7d)
        //         |---------------------|
        // 0d --- now --- 1d ---  ... --- 7d
        parse("+7d/now", "[2016-06-26 01:23:45-0700,2016-07-03 00:00:00-0700)");

        // [-1h, 0h)
        parse("-1h", "[2016-06-26 00:00:00-0700,2016-06-26 01:00:00-0700)");
        // [-1h, now)
        parse("-1h/now", "[2016-06-26 00:00:00-0700,2016-06-26 01:23:45-0700)");
        parse("-1h/0m", "[2016-06-26 00:00:00-0700,2016-06-26 01:23:00-0700)");

        // -12h/now  (last 12 hours + fraction until now)

        parse("-12h/now", "[2016-06-25 13:00:00-0700,2016-06-26 01:23:45-0700)");
        parse("-12h", "[2016-06-25 13:00:00-0700,2016-06-26 01:00:00-0700)");
        parse("-12h/now", "[2016-06-25 13:00:00-0700,2016-06-26 01:23:45-0700)");
        parse("+12h/now", "[2016-06-26 01:23:45-0700,2016-06-26 13:00:00-0700)");

        // Absolute offset
        // 3d:2017-04-07 [2017-04-04,2017-04-07)
        parse("-3d/2017-04-07", "[2017-04-04 00:00:00-0700,2017-04-07 00:00:00-0700)");

        // The offset can be specified using a duration
        // -1M:-1M  [2017-04-01, 2017-05-01) if today is 2017-05-20
        parse("-1M/0M", "[2016-05-01 00:00:00-0700,2016-06-01 00:00:00-0700)");
        // -1M:-1M  [2017-03-01, 2017-04-01) if today is 2017-05-20
        parse("-1M/-1M", "[2016-04-01 00:00:00-0700,2016-05-01 00:00:00-0700)");
        parse("-1M/lastMonth", "[2016-04-01 00:00:00-0700,2016-05-01 00:00:00-0700)");

        // Offset dates can be arbitrary time units
        parse("-1M/2018-09-02", "[2018-08-01 00:00:00-0700,2018-09-01 00:00:00-0700)");
        parse("-1M/2018-09-02 01:12:13", "[2018-08-01 00:00:00-0700,2018-09-01 00:00:00-0700)");
        parse("-1h/2017-01-23 01:00:00", "[2017-01-23 00:00:00-0700,2017-01-23 01:00:00-0700)");
        parse("-1h/2017-01-23 01:23:45", "[2017-01-23 00:00:00-0700,2017-01-23 01:00:00-0700)");
        parse("-60m/2017-01-23 01:23:45", "[2017-01-23 00:23:00-0700,2017-01-23 01:23:00-0700)");

        // Untruncate offset if it ends with ")"
        parse("0M/2018-09-02)", "[2018-09-01 00:00:00-0700,2018-09-02 00:00:00-0700)");
        parse("-1M/2018-09-02 12:34:56)", "[2018-08-01 00:00:00-0700,2018-09-02 12:34:56-0700)");
        parse("+1M/2018-09-02)", "[2018-09-02 00:00:00-0700,2018-10-01 00:00:00-0700)");

        // If different units are used for duration and offset, try to extend to the range to the offset unit
        parse("-1M/0d", "[2016-05-01 00:00:00-0700,2016-06-26 00:00:00-0700)");
        parse("-1M/0h", "[2016-05-01 00:00:00-0700,2016-06-26 01:00:00-0700)");
        parse("-1M/0m", "[2016-05-01 00:00:00-0700,2016-06-26 01:23:00-0700)");
        parse("-1M/0s", "[2016-05-01 00:00:00-0700,2016-06-26 01:23:45-0700)");

        // quarter
        parse("-1q", "[2016-01-01 00:00:00-0700,2016-04-01 00:00:00-0700)");
        parse("1q", "[2016-04-01 00:00:00-0700,2016-07-01 00:00:00-0700)");
        parse("-2q", "[2015-10-01 00:00:00-0700,2016-04-01 00:00:00-0700)");
        parse("-1q/0y", "[2015-10-01 00:00:00-0700,2016-01-01 00:00:00-0700)");
    }

    @Test
    public void supportHumanFriendlyRange() {
        parse("today", "[2016-06-26 00:00:00-0700,2016-06-27 00:00:00-0700)");
        parse("today/now", "[2016-06-26 00:00:00-0700,2016-06-26 01:23:45-0700)");
        parse("thisHour", "[2016-06-26 01:00:00-0700,2016-06-26 02:00:00-0700)");
        parse("thisWeek", "[2016-06-20 00:00:00-0700,2016-06-27 00:00:00-0700)");
        parse("thisMonth", "[2016-06-01 00:00:00-0700,2016-07-01 00:00:00-0700)");
        parse("thisMonth/now", "[2016-06-01 00:00:00-0700,2016-06-26 01:23:45-0700)");
        parse("thisYear", "[2016-01-01 00:00:00-0700,2017-01-01 00:00:00-0700)");

        parse("yesterday", "[2016-06-25 00:00:00-0700,2016-06-26 00:00:00-0700)");
        parse("yesterday/now", "[2016-06-25 00:00:00-0700,2016-06-26 01:23:45-0700)");
        parse("lastHour", "[2016-06-26 00:00:00-0700,2016-06-26 01:00:00-0700)");
        parse("lastWeek", "[2016-06-13 00:00:00-0700,2016-06-20 00:00:00-0700)");
        parse("lastMonth", "[2016-05-01 00:00:00-0700,2016-06-01 00:00:00-0700)");
        parse("lastYear", "[2015-01-01 00:00:00-0700,2016-01-01 00:00:00-0700)");

        parse("tomorrow", "[2016-06-27 00:00:00-0700,2016-06-28 00:00:00-0700)");
        parse("tomorrow/now", "[2016-06-26 01:23:45-0700,2016-06-28 00:00:00-0700)");
        parse("nextHour", "[2016-06-26 02:00:00-0700,2016-06-26 03:00:00-0700)");
        parse("nextWeek", "[2016-06-27 00:00:00-0700,2016-07-04 00:00:00-0700)");
        parse("nextMonth", "[2016-07-01 00:00:00-0700,2016-08-01 00:00:00-0700)");
        parse("nextYear", "[2017-01-01 00:00:00-0700,2018-01-01 00:00:00-0700)");
    }

    @Test
    public void splitTimeWindow() {
        List<TimeWindow> weeks = t.parse("5w").splitIntoWeeks();
        System.out.println(weeks);

        List<TimeWindow>  weeks2 = t.parse("-5w/2017-06-01").splitIntoWeeks();
        System.out.println(weeks2);

        List<TimeWindow>  months = t.parse("thisYear/thisMonth").splitIntoMonths();
        System.out.println(months);
        List<TimeWindow>  months2 = t.parse("thisYear/0M").splitIntoMonths();
        System.out.println(months2);

        List<TimeWindow>  days = t.parse("thisMonth").splitIntoWeeks();
        System.out.println(days);
    }

    @Test
    public void parseTimeZone() {
        // Sanity tests
        TimeWindow.withTimeZone("UTC");
        TimeWindow.withTimeZone("PST");
        TimeWindow.withTimeZone("PDT");
        TimeWindow.withTimeZone("JST");
        TimeWindow.withTimeZone("EDT");
        TimeWindow.withTimeZone("BST");
        TimeWindow.withTimeZone("CDT");
        TimeWindow.withTimeZone("MDT");
    }

    @Test
    public void useProperTimeZone() {
        TimeZone defaultZone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            ZonedDateTime t = TimeParser.parse("2017-04-04", ZoneOffset.of("-07:00"));
            System.out.println(t);
            TimeWindowBuilder w = TimeWindow.withTimeZone("PDT");
            TimeWindow d = w.parse("-3d/2017-04-07");
            System.out.println(d);
        } finally {
            TimeZone.setDefault(defaultZone);
        }
    }


}
