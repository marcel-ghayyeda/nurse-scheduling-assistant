package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.Duration;

class RestTime {

    private final Duration duration;

    private RestTime(int hours) {
        this.duration = Duration.ofHours(hours);
    }

    static RestTime hours(int hours) {
        return new RestTime(hours);
    }

    Duration getDuration() {
        return duration;
    }
}
