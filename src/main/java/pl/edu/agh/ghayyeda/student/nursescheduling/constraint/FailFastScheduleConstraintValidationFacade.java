package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

@Component
public class FailFastScheduleConstraintValidationFacade implements ScheduleConstraintValidationFacade {

    private final FailFastScheduleConstraintFactory failFastScheduleConstraintFactory;

    public FailFastScheduleConstraintValidationFacade(FailFastScheduleConstraintFactory failFastScheduleConstraintFactory) {
        this.failFastScheduleConstraintFactory = failFastScheduleConstraintFactory;
    }

    @Override
    public ScheduleConstraintValidationResult validate(Schedule schedule) {
        var validationStartTime = LocalDateTime.of(LocalDate.of(schedule.getYear().getValue(), schedule.getMonth(), 1), Shift.DAY.getStartTime());
        var yearMonth = YearMonth.of(schedule.getYear().getValue(), schedule.getMonth());
        var validationEndTime = LocalDateTime.of(yearMonth.atDay(yearMonth.lengthOfMonth()), LocalTime.of(23, 59));

        return validate(schedule, validationStartTime, validationEndTime);
    }

    @Override
    public ScheduleConstraintValidationResult validate(Schedule schedule, LocalDateTime validationStartTime, LocalDateTime validationEndTime) {
        var scheduleConstraints = failFastScheduleConstraintFactory.get(validationStartTime, validationEndTime, schedule.getNumberOfChildren());

        for (ScheduleConstraint scheduleConstraint : scheduleConstraints) {
            var validationResult = scheduleConstraint.validate(schedule);
            if (!validationResult.isFeasible()) {
                System.out.println(String.format("Not feasible due to %s constraint", scheduleConstraint.getClass()));
                return ScheduleConstraintValidationResult.notFeasibleConstraintValidationResult();
            }
        }
        return ScheduleConstraintValidationResult.feasibleConstraintValidationResult();
    }

}
