package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.Duration;
import java.time.LocalTime;

public enum Shift {

    R(7, 15),
    P(15, 19),
    D(7, 19),
    N(19, 7),
    DN(7, 7),
    W();

    private final int startTime;
    private final int endTime;
    private final boolean workDay;

    Shift(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.workDay = true;
    }

    Shift() {
        this.workDay = false;
        this.startTime = 0;
        this.endTime = 0;
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

    public boolean isWorkDay() {
        return workDay;
    }

    private static LocalTime timeOfHour(int i) {
        return LocalTime.of(i, 0);

    }
}
