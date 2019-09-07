package pl.edu.agh.ghayyeda.student.nursescheduling.schedule.neighbourhood;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintViolationsDescription;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.EmployeeDateViolation;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.util.Collection;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;

public class AdaptiveLargeNeighbourhoodStrategy extends AbstractNeighbourhoodStrategy implements NeighbourhoodStrategy {

    private static final Logger log = LoggerFactory.getLogger(AdaptiveLargeNeighbourhoodStrategy.class);

    private final Adaptation adaptation;

    public AdaptiveLargeNeighbourhoodStrategy(Adaptation adaptation) {
        this.adaptation = adaptation;
    }

    @Override
    public Stream<Schedule> createNeighbourhood(Schedule schedule, ConstraintValidationResult constraintValidationResult) {
        var employeeDateViolationsByDate = constraintValidationResult.getConstraintViolationsDescriptions()
                .stream()
                .map(ConstraintViolationsDescription::getEmployeeDateViolations)
                .flatMap(Collection::stream)
                .collect(groupingBy(EmployeeDateViolation::getDate));

        return Stream.of(swapShiftsInTheSameDaysBetweenEmployees(schedule, employeeDateViolationsByDate, adaptation), addWorkingShifts(schedule, employeeDateViolationsByDate, adaptation), removeShifts(schedule, employeeDateViolationsByDate, adaptation))
                .flatMap(identity())
                .distinct();
    }

}
