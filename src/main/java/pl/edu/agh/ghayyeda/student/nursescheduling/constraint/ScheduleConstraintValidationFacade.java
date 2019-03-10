package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

@Component
public class ScheduleConstraintValidationFacade {

    private final StaticScheduleConstraintFactory staticScheduleConstraintFactory;

    public ScheduleConstraintValidationFacade(StaticScheduleConstraintFactory staticScheduleConstraintFactory) {
        this.staticScheduleConstraintFactory = staticScheduleConstraintFactory;
    }

    public ScheduleConstraintValidationResult validate(Schedule schedule) {
        var validationStartTime = LocalDateTime.of(LocalDate.of(schedule.getYear().getValue(), schedule.getMonth(), 1), Shift.DAY.getStartTime());
        var yearMonth = YearMonth.of(schedule.getYear().getValue(), schedule.getMonth());
        var validationEndTime = LocalDateTime.of(yearMonth.atDay(yearMonth.lengthOfMonth()), LocalTime.of(23, 59));

        return validate(schedule, validationStartTime, validationEndTime);
    }

    @VisibleForTesting
    public ScheduleConstraintValidationResult validate(Schedule schedule, LocalDateTime validationStartTime, LocalDateTime validationEndTime) {
        var scheduleConstraints = staticScheduleConstraintFactory.get(validationStartTime, validationEndTime, schedule.getNumberOfChildren());

        for (ScheduleConstraint scheduleConstraint : scheduleConstraints) {
            var validationResult = scheduleConstraint.validate(schedule);
            if (!validationResult.isFeasible()) {
                System.out.println(String.format("Not feasible due to %s constraint", scheduleConstraint.getClass()));
                return HardConstraintValidationResult.notFeasibleConstraintValidationResult();
            }
        }
        return HardConstraintValidationResult.feasibleConstraintValidationResult();
    }

}
