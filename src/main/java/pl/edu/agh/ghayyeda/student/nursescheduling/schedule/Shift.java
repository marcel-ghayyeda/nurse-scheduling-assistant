package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.Duration;
import java.time.LocalTime;

public enum Shift {
    //    R - 8h od 7 do 15 - min. przerwa 11h
//    P - 4h od 15 do 19 - min. przerwa 11h
//    D - 12h od 7 do 19 - min. przerwa 11h
//    N - 12h od 19 do 7 - min. przerwa 11h
//    DN - 24h od 7 do 7 - min. przerwa 24h
    R(timeOfHour(7), timeOfHour(15)),
    P(timeOfHour(15), timeOfHour(19)),
    D(timeOfHour(7), timeOfHour(19)),
    N(timeOfHour(19), timeOfHour(7)),
    DN(timeOfHour(7), timeOfHour(7)),
    W(timeOfHour(0), timeOfHour(0)); //TODO day off

    private final LocalTime startTime;
    private final LocalTime endTime;

    Shift(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    private static LocalTime timeOfHour(int i) {
        return LocalTime.of(i, 0);

    }
}
