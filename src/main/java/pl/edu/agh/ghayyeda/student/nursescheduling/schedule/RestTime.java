package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.Duration;

public class RestTime {

    private final int hours;

    private RestTime(int hours) {
        this.hours = hours;
    }

    public static RestTime hours(int hours) {
        return new RestTime(hours);
    }

    public Duration getDuration() {
        return Duration.ofHours(hours);
    }
}
