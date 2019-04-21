package pl.edu.agh.ghayyeda.student.nursescheduling.util;

import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ScheduleValidationUtils {

    public static LocalDateTime getStandardValidationStartTime(Schedule schedule) {
        return LocalDateTime.of(LocalDate.of(schedule.getYear().getValue(), schedule.getMonth(), 1), Shift.DAY.getStartTime());
    }

    public static LocalDateTime getStandardValidationEndTime(Schedule schedule) {
        var yearMonth = schedule.getYearMonth();
        return LocalDateTime.of(yearMonth.atDay(yearMonth.lengthOfMonth()), LocalTime.of(23, 59));
    }
}
