package com.github.takezoe.jtimewindow;

import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TimeParserTest {

    public void parse(String str, String expected){
        ZonedDateTime z   = TimeParser.parse(str, Constants.UTC);
        ZonedDateTime ans = ZonedDateTime.parse(expected);

        if (z == null) {
            fail("failed to parse " + str);
        }

        assertEquals(TimeStampFormatter.formatTimestamp(ans), TimeStampFormatter.formatTimestamp(z));
    }

    @Test
    public void parseDateTime(){
        // Time with time zone
        parse("2017-01-01 23:01:23-0700", "2017-01-01T23:01:23-07:00");
        parse("2017-01-01 23:01:23-07:00", "2017-01-01T23:01:23-07:00");
        parse("2017-01-01 00:00:00 UTC", "2017-01-01T00:00:00Z");
        parse("2017-01-01 01:23:45Z", "2017-01-01T01:23:45Z");
        parse("2017-01-01 01:23:45+0900", "2017-01-01T01:23:45+09:00");

        // PDT
        parse("2017-01-01 00:00:00 America/Los_Angeles", "2017-01-01T00:00:00-08:00");

        // PST
        parse("2017-05-01 00:00:00 America/Los_Angeles", "2017-05-01T00:00:00-07:00");

        // Date only strings
        // UTC
        parse("2017-01-01", "2017-01-01T00:00:00Z");
        parse("2016-12-01", "2016-12-01T00:00:00Z");

        // Datetime without time zone
        parse("2016-12-01 08:00:01", "2016-12-01T08:00:01Z");
        parse("2016-12-01 08:00:01", "2016-12-01T08:00:01Z");
    }



}
