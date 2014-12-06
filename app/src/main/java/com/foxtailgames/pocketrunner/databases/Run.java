package com.foxtailgames.pocketrunner.databases;

import com.foxtailgames.pocketrunner.Time;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Ricardo on 02/12/2014.
 * @author Ricardo Delfin Garcia
 * @version 1.0
 */
public class Run {
    public UUID id;
    public Date date;
    public double distance;
    public String units;
    public Time time;
    public long[] lapTimes;

    public Run() {
        this(new Date(), 0, "", new Time(), new long[]{});
    }

    public Run(UUID id, Date date, double distance, String units, Time time, long[] lapTimes) {
        this.id = id;
        this.date = date;
        this.distance = distance;
        this.units = units;
        this.time = time;
        this.lapTimes = lapTimes;
    }

    public Run(Date date, double distance, String units, Time time, long[] lapTimes) {
        this(UUID.randomUUID(), date, distance, units, time, lapTimes);
    }

}