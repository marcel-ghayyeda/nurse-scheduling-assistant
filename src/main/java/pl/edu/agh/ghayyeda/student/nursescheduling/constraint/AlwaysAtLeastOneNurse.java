package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignment;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;
import pl.edu.agh.ghayyeda.student.nursescheduling.util.YearMonthUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static pl.edu.agh.ghayyeda.student.nursescheduling.constraint.HardConstraintValidationResult.feasibleConstraintValidationResult;
import static pl.edu.agh.ghayyeda.student.nursescheduling.constraint.HardConstraintValidationResult.notFeasibleConstraintValidationResult;

public class AlwaysAtLeastOneNurse implements ScheduleConstraint {

    private static final Logger log = LoggerFactory.getLogger(AlwaysAtLeastOneNurse.class);
    private final LocalDateTime validationStartTime;
    private final LocalDateTime validationEndTime;
    private final YearMonth validationMonth;

    private AlwaysAtLeastOneNurse(LocalDateTime validationStartTime, LocalDateTime validationEndTime) {
        assert validationStartTime.getMonth() == validationEndTime.getMonth();
        this.validationStartTime = validationStartTime;
        this.validationEndTime = validationEndTime;
        this.validationMonth = YearMonth.from(validationStartTime);
    }

    public static AlwaysAtLeastOneNurse between(LocalDateTime validationStartTime, LocalDateTime validationEndTime) {
        return new AlwaysAtLeastOneNurse(validationStartTime, validationEndTime);
    }

    @Override
    public ScheduleConstraintValidationResult validate(Schedule schedule) {
        var isFeasible = YearMonthUtil.allDaysOf(validationMonth)
                .flatMap(dayOfMonth -> allHoursADay().mapToObj(toLocalDateTimeOn(dayOfMonth)))
                .filter(notBeforeValidationStartTime())
                .filter(beforeValidationEndtime())
                .allMatch(timeOfDuty -> {
                    boolean feasible = schedule.getEmployeeShiftAssignmentsFor(timeOfDuty).anyMatch(isNurse());
                    if(!feasible){
                        log.debug("No nurse on " + timeOfDuty);
                    }
                    return feasible;
                });

        return isFeasible ? feasibleConstraintValidationResult() : notFeasibleConstraintValidationResult();
    }

    private IntFunction<LocalDateTime> toLocalDateTimeOn(LocalDate dayOfMonth) {
        return hour -> LocalDateTime.of(dayOfMonth, LocalTime.of(hour, 0));
    }

    private Predicate<LocalDateTime> notBeforeValidationStartTime() {
        return localDateTime -> !localDateTime.isBefore(validationStartTime);
    }

    private Predicate<? super LocalDateTime> beforeValidationEndtime() {
        return localDateTime -> localDateTime.isBefore(validationEndTime);
    }

    private IntStream allHoursADay() {
        return IntStream.rangeClosed(0, 23);
    }

    private Predicate<? super EmployeeShiftAssignment> isNurse() {
        return dayShiftAssignment -> Employee.Type.NURSE == dayShiftAssignment.getEmployeeType();
    }

}
