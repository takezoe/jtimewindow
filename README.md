# JTimeWindow

A Java library to parse the time range expressions, a Java port of [airframe-metrics](https://github.com/wvlet/airframe/tree/master/airframe-metrics).

## Usage

Add a following dependency to your `pom.xml`:

```xml
<dependencies>
  <dependency>
    <groupId>com.github.takezoe</groupId>
    <artifactId>jtimewindow</artifactId>
    <version>1.0.0</version>
  </dependency>
</dependencies>
```

Then you can parse the time range expressions and get [TimeWindow](https://github.com/takezoe/jtimewindow/blob/master/src/main/java/com/github/takezoe/jtimewindow/TimeWindow.java) as follows:

```java
import com.github.takezoe.jtimewindow.*;

TimeWindowBuilder t = TimeWindow.withTimeZone("UTC");
TimeWindow w = t.parse("-7d");

ZonedDateTime start = w.getStart();
ZonedDateTime end = w.getEnd();
```

See the [airframe-metrics documentation](https://wvlet.org/airframe/docs/airframe-metrics.html#examples) to know all supported expressions.
