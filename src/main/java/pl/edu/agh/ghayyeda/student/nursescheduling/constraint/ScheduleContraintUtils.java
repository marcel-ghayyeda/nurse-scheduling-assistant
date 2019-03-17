package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import java.util.stream.IntStream;

class ScheduleContraintUtils {

    static IntStream significantHoursOfDay() {
        return IntStream.of(8, 16, 23);
    }

}
