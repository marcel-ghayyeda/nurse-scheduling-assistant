package pl.edu.agh.ghayyeda.student.nursescheduling.constraint.penaltyaware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraint;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.IntStream;

import static pl.edu.agh.ghayyeda.student.nursescheduling.util.Predicates.not;

public class MinimumOvertime implements ScheduleConstraint {

    private static final Logger log = LoggerFactory.getLogger(MinimumOvertime.class);

    @Override
    public ConstraintValidationResult validate(Schedule schedule) {
        Set<DayOfWeek> weekend = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        YearMonth yearMonth = schedule.getYearMonth();
        int fullTimeWorkHoursPerMonth = IntStream.rangeClosed(1, yearMonth.lengthOfMonth())
                .mapToObj(yearMonth::atDay)
                .map(LocalDate::getDayOfWeek)
                .filter(not(weekend::contains))
                .mapToInt(__ -> 8)
                .sum();

        double penalty = schedule.getWorkHoursPerEmployee().values().stream()
                .filter(numberOfHours -> numberOfHours > fullTimeWorkHoursPerMonth)
                .mapToDouble(numberOfHours -> (numberOfHours - fullTimeWorkHoursPerMonth) * 0.000001)
                .sum();

        log.debug("Total penalty: {}", penalty);
        return ConstraintValidationResult.feasibleConstraintValidationResult(penalty);
    }
}
