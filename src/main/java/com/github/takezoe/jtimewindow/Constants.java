package com.github.takezoe.jtimewindow;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Port of airframe-metrics
 */
public class Constants {
    // Need to get the current ZoneOffset to resolve PDT, etc.
    // because ZoneID of America/Los Angels (PST) is -0800 while PDT zone offset is -0700
    public static final ZoneOffset SystemTimeZone = ZonedDateTime.now(ZoneId.systemDefault().normalized()).getOffset();
    public static final ZoneOffset UTC = ZoneOffset.UTC;

}
