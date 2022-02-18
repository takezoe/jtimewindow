# JTimeWindow [![Build](https://github.com/takezoe/jtimewindow/actions/workflows/maven.yml/badge.svg)](https://github.com/takezoe/jtimewindow/actions/workflows/maven.yml) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.takezoe/jtimewindow/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.takezoe/jtimewindow) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/takezoe/jtimewindow/blob/master/LICENSE)

A Java library to parse time range expressions, port of [airframe-metrics](https://github.com/wvlet/airframe/tree/master/airframe-metrics).

## Usage

Add a following dependency to your `pom.xml`:

```xml
<dependencies>
  <dependency>
    <groupId>com.github.takezoe</groupId>
    <artifactId>jtimewindow</artifactId>
    <version>1.3.0</version>
  </dependency>
</dependencies>
```

Then you can parse the time range expressions and get [TimeWindow](https://github.com/takezoe/jtimewindow/blob/master/src/main/java/com/github/takezoe/jtimewindow/TimeWindow.java) as follows:

```java
import com.github.takezoe.jtimewindow.*;

TimeWindowBuilder t = TimeWindow.withTimeZone("PDT").withOffset("2016-06-26 01:23:45-0700");
TimeWindow w = t.parse("-7d");

ZonedDateTime start = w.getStart();
ZonedDateTime end = w.getEnd();
```

## Examples

Here are examples of the relative time range expression when the current time is `2016-06-26 01:23:45-0700`:

|Duration                  |Definition                                   |start                     |end (exclusive)           |
|--------------------------|---------------------------------------------|--------------------------|--------------------------|
|`1h`                      |this hour                                    |`2016-06-26 01:00:00-0700`|`2016-06-26 02:00:00-0700`|
|`1d`                      |today                                        |`2016-06-26 00:00:00-0700`|`2016-06-27 00:00:00-0700`|
|`1M`                      |this month                                   |`2016-06-01 00:00:00-0700`|`2016-07-01 00:00:00-0700`|
|`-1h`                     |last hour                                    |`2016-06-26 00:00:00-0700`|`2016-06-26 01:00:00-0700`|
|`-1h/now`                 |last hour to now                             |`2016-06-26 00:00:00-0700`|`2016-06-26 01:23:45-0700`|
|`-60m/2017-01-23 01:23:45`|last 60 minutes to the given offset minute   |`2017-01-23 00:23:00-0700`|`2017-01-23 01:23:00-0700`|
|`-1d`                     |last day                                     |`2016-06-25 00:00:00-0700`|`2016-06-26 00:00:00-0700`|
|`-7d`                     |last 7 days                                  |`2016-06-19 00:00:00-0700`|`2016-06-26 00:00:00-0700`|
|`-7d/now`                 |last 7 days to now                           |`2016-06-10 00:00:00-0700`|`2016-06-26 01:23:45-0700`|
|`-3d/2017-04-07`          |last 3 days from a given offset              |`2017-04-04 00:00:00-0700`|`2017-04-07 00:00:00-0700`|
|`+7d`                     |next 7 days (including today)                |`2016-06-26 00:00:00-0700`|`2016-07-03 00:00:00-0700`|
|`+7d/now`                 |next 7 days from now                         |`2016-06-26 01:23:45-0700`|`2016-07-03 00:00:00-0700`|
|`-1w`                     |last week                                    |`2016-06-13 00:00:00-0700`|`2016-06-20 00:00:00-0700`|
|`-1M`                     |last month                                   |`2016-05-01 00:00:00-0700`|`2016-06-01 00:00:00-0700`|
|`-1q`                     |last quarter                                 |`2016-01-01 00:00:00-0700`|`2016-04-01 00:00:00-0700`|
|`-1y`                     |last year                                    |`2015-01-01 00:00:00-0700`|`2016-01-01 00:00:00-0700`|
|`-1h/2017-01-23 01:00:00` |last hour from the given offset (hour)       |`2017-01-23 00:00:00-0700`|`2017-01-23 01:00:00-0700`|
|`-1h/2017-01-23 01:23:45` |last hour from the given offset (hour)       |`2017-01-23 00:00:00-0700`|`2017-01-23 01:00:00-0700`|
|`-1M/2017-01-23 01:23:45` |last month from the given offset (hour)      |`2016-12-01 00:00:00-0700`|`2017-01-01 00:00:00-0700`|
|`0M/2017-01-23)`          |from beginning of the month to a given offset|`2017-01-01 00:00:00-0700`|`2017-01-23 00:00:00-0700`|
|`+1M/2017-01-23 01:23:45)`|from a given offset to end of the month      |`2017-01-23 01:23:45-0700`|`2017-02-01 00:00:00-0700`|
