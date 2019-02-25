package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Stream;

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

    Shift(int startTime, int endTime, RestTime restTime, String localizedShiftSymbol) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.restTime = restTime;
        this.workDay = true;
        this.localizedShiftSymbol = localizedShiftSymbol;
    }

    Shift(String localizedShiftSymbol) {
        this.workDay = false;
        this.startTime = 0;
        this.endTime = 0;
        this.restTime = RestTime.hours(0);
        this.localizedShiftSymbol = localizedShiftSymbol;
    }

    public static Stream<Shift> allWorkingShifts() {
        return Arrays.stream(values())
                .filter(Shift::isWorkDay);
    }

    public Duration getDuration() {
        if (isWorkDay())
            if (endsOnNextDay())
                return Duration.between(LocalDateTime.of(LocalDate.now(), getStartTime()),
                        LocalDateTime.of(LocalDate.now().plusDays(1), getStartTime()));
            else return Duration.ofHours(Math.abs(endTime - startTime));
        else return Duration.ZERO;
    }

    public LocalTime getStartTime() {
        return timeOfHour(startTime);
    }

    public LocalTime getEndTime() {
        return timeOfHour(endTime);
    }

    public boolean endsOnNextDay() {
        return getEndTime().isBefore(getStartTime()) || getEndTime().equals(getStartTime());
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
}
