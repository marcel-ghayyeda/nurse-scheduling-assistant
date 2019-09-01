package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public enum Shift {

    MORNING(7, 15, RestTime.hours(11), "R"),
    AFTERNOON(15, 19, RestTime.hours(11), "P"),
    DAY(7, 19, RestTime.hours(11), "D"),
    NIGHT(19, 7, RestTime.hours(11), "N"),
    DAY_NIGHT(7, 7, RestTime.hours(24), "DN"),
    DAY_OFF("W"),
    VACATION("UR"),
    SICK_LEAVE("L4");

    private final int startTime;
    private final int endTime;
    private final boolean workDay;
    private final RestTime restTime;
    private final String localizedShiftSymbol;
    private final Duration duration;
    private final boolean endsOnNextDay;
    private final LocalTime startLocalTime;
    private final LocalTime endLocalTime;

    Shift(int startTime, int endTime, RestTime restTime, String localizedShiftSymbol) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.restTime = restTime;
        this.workDay = true;
        this.localizedShiftSymbol = localizedShiftSymbol;
        this.startLocalTime = timeOfHour(startTime);
        this.endLocalTime = timeOfHour(endTime);
        this.endsOnNextDay = calculateEndsOnNextDay();
        this.duration = calculateDuration();
    }

    Shift(String localizedShiftSymbol) {
        this.workDay = false;
        this.startTime = 0;
        this.endTime = 0;
        this.restTime = RestTime.hours(0);
        this.localizedShiftSymbol = localizedShiftSymbol;
        this.duration = Duration.ZERO;
        this.endsOnNextDay = false;
        this.startLocalTime = timeOfHour(startTime);
        this.endLocalTime = timeOfHour(endTime);
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalTime getStartTime() {
        return startLocalTime;
    }

    public LocalTime getEndTime() {
        return endLocalTime;
    }

    public boolean endsOnNextDay() {
        return endsOnNextDay;
    }

    public Duration getRestTime() {
        return restTime.getDuration();
    }

    public boolean isWorkDay() {
        return workDay;
    }

    public boolean isDayOff() {
        return DAY_OFF == this;
    }

    private static LocalTime timeOfHour(int i) {
        return LocalTime.of(i, 0);
    }

    public String getLocalizedShiftSymbol() {
        return localizedShiftSymbol;
    }

    private Duration calculateDuration() {
        if (isWorkDay()) {
            if (endsOnNextDay()) {
                return Duration.between(LocalDateTime.of(LocalDate.now(), getStartTime()),
                        LocalDateTime.of(LocalDate.now().plusDays(1), getEndTime()));
            } else return Duration.ofHours(Math.abs(endTime - startTime));
        } else return Duration.ZERO;
    }

    private boolean calculateEndsOnNextDay() {
        return getEndTime().isBefore(getStartTime()) || getEndTime().equals(getStartTime());
    }
}
