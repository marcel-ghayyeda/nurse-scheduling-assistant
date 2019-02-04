package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.stream.Collectors.toList;

public enum Shift {

    R(7, 15, RestTime.hours(11)),
    P(15, 19, RestTime.hours(11)),
    D(7, 19, RestTime.hours(11)),
    N(19, 7, RestTime.hours(11)),
    DN(7, 7, RestTime.hours(24)),
    W();

    private final int startTime;
    private final int endTime;
    private final boolean workDay;
    private final RestTime restTime;

    Shift(int startTime, int endTime, RestTime restTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.restTime = restTime;
        this.workDay = true;
    }

    Shift() {
        this.workDay = false;
        this.startTime = 0;
        this.endTime = 0;
        this.restTime = RestTime.hours(0);
    }

    public static Shift randomWorkShift() {
        var workShifts = Arrays.stream(values()).filter(Shift::isWorkDay).collect(toList());
        return workShifts.get(ThreadLocalRandom.current().nextInt(workShifts.size()));
    }

    public Duration getDuration() {
        return isWorkDay() ? Duration.ofHours(Math.abs(endTime - startTime)) : Duration.ZERO;
    }

    public LocalTime getStartTime() {
        return timeOfHour(startTime);
    }

    public LocalTime getEndTime() {
        return timeOfHour(endTime);
    }

    public Duration getRestTime(){
        return restTime.getDuration();
    }

    public boolean isWorkDay() {
        return workDay;
    }

    private static LocalTime timeOfHour(int i) {
        return LocalTime.of(i, 0);
    }
}
