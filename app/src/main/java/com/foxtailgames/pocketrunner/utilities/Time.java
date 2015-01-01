package com.foxtailgames.pocketrunner.utilities;

import java.text.DecimalFormat;

/**
 * Basic class to store time, in hours minutes and seconds. The actual representation is in
 * milliseconds, but the getters return hours, minutes, seconds, and milliseconds (or tenths of a
 * second if you use the getTenths method instead)
 * @author Ricardo Delfin Garcia
 * @version 1.0
 */
public class Time {
    long milliseconds;

    public Time() {
        this(0);
    }

    public Time(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public Time(long hours, long minutes, long seconds, long milliseconds) {
        this(milliseconds + (seconds + (minutes + (hours) * 60) * 60) * 1000);
    }

    public Time(long hours, long minutes, long seconds) {
        this(hours, minutes, seconds, 0);
    }

    public long getHours() { return milliseconds / 3600000; }
    public long getMinutes() { return (milliseconds / 60000) % 60; }
    public long getSeconds() { return (milliseconds / 1000) % 60; }
    public long getMilliseconds() { return milliseconds % 1000; }
    public long getTenths() { return (milliseconds / 100) % 10; }

    public long getTotalMilliseconds() { return milliseconds; }

    public String toString() {
        String result = "";
        DecimalFormat df = new DecimalFormat("00");
        long hours = getHours();
        long minutes = getMinutes();
        long seconds = getSeconds();
        long tenths = getTenths();

        if(hours > 0)
            result += hours + ":";

        result += df.format(minutes) + ":";
        result += df.format(seconds) + ".";
        result += tenths;

        return result;
    }

    public void addTime(long millToAdd) {
        this.milliseconds += millToAdd;
    }

    public void addTime(long hours, long minutes, long seconds, long milliseconds) {
        this.milliseconds += milliseconds;
        this.milliseconds += seconds * 1000;
        this.milliseconds += minutes * 60000;
        this.milliseconds += hours * 3600000;
    }

    public boolean greaterThan(Time t) { return milliseconds > t.milliseconds; }
    public boolean lessThan(Time t) { return milliseconds < t.milliseconds; }
    public boolean greaterThanOrEqual(Time t) { return !lessThan(t); }
    public boolean lessThanOrEqual(Time t) { return !greaterThan(t); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Time time = (Time)o;

        return milliseconds == time.milliseconds;
    }

    @Override
    public int hashCode() {
        return (int) (milliseconds ^ (milliseconds >>> 32));
    }
}
