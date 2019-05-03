package pl.edu.agh.ghayyeda.student.nursescheduling.constraint.penaltyaware;

import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationFacade;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static pl.edu.agh.ghayyeda.student.nursescheduling.util.Predicates.not;

@Component
public class PenaltyAwareScheduleConstraintValidationFacade implements ScheduleConstraintValidationFacade {

    private final PenaltyAwareScheduleConstraintFactory scheduleConstraintFactory;

    public PenaltyAwareScheduleConstraintValidationFacade(PenaltyAwareScheduleConstraintFactory scheduleConstraintFactory) {
        this.scheduleConstraintFactory = scheduleConstraintFactory;
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
        var scheduleConstraints = scheduleConstraintFactory.get(validationStartTime, validationEndTime, schedule.getNumberOfChildren());
        var validationResults = scheduleConstraints.stream()
                .parallel()
                .map(scheduleConstraint -> scheduleConstraint.validate(schedule))
                .collect(toList());

        var sumOfPenalties = validationResults.stream()
                .mapToDouble(ScheduleConstraintValidationResult::getPenalty)
                .sum();

        var isNotFeasible = validationResults.stream().anyMatch(not(ScheduleConstraintValidationResult::isFeasible));
        List<String> descriptions = validationResults.stream().map(ScheduleConstraintValidationResult::getDescriptions).flatMap(Collection::stream).collect(toList());

        return isNotFeasible ?
                ScheduleConstraintValidationResult.ofPenalty(sumOfPenalties, descriptions) :
                ScheduleConstraintValidationResult.feasibleConstraintValidationResult(sumOfPenalties);
    }
}
