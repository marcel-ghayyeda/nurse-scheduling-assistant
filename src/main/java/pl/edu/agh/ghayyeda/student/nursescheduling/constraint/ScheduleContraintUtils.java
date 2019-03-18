package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import java.util.stream.IntStream;

public class ScheduleContraintUtils {

    public static IntStream significantHoursOfDay() {
        return IntStream.of(8, 16, 23);
    }

}
