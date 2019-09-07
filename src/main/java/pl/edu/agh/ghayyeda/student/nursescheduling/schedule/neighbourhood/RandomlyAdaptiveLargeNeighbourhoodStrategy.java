package pl.edu.agh.ghayyeda.student.nursescheduling.schedule.neighbourhood;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintViolationsDescription;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.EmployeeDateViolation;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;

public class RandomlyAdaptiveLargeNeighbourhoodStrategy extends AbstractNeighbourhoodStrategy implements NeighbourhoodStrategy {

    private final Adaptation adaptation;

    RandomlyAdaptiveLargeNeighbourhoodStrategy(Adaptation adaptation) {
        this.adaptation = adaptation;
    }

    private static final Logger log = LoggerFactory.getLogger(RandomlyAdaptiveLargeNeighbourhoodStrategy.class);

    @Override
    public Stream<Schedule> createNeighbourhood(Schedule schedule, ConstraintValidationResult constraintValidationResult) {

        Collection<ConstraintViolationsDescription> constraintValidationDescriptions;
        int maxAllowedContraintviolationDescriptionsSize = adaptation == Adaptation.WIDE ? 4 : 2;
        if (constraintValidationResult.getConstraintViolationsDescriptions().size() > maxAllowedContraintviolationDescriptionsSize) {
            var originalConstraintValidationDescriptions = new ArrayList<>(constraintValidationResult.getConstraintViolationsDescriptions());
            Collections.shuffle(originalConstraintValidationDescriptions);
            constraintValidationDescriptions = rangeClosed(0, maxAllowedContraintviolationDescriptionsSize - 1)
                    .mapToObj(originalConstraintValidationDescriptions::get)
                    .collect(toList());
        } else {
            constraintValidationDescriptions = constraintValidationResult.getConstraintViolationsDescriptions();
        }
        var employeeDateViolationsByDate = constraintValidationDescriptions.stream()
                .map(ConstraintViolationsDescription::getEmployeeDateViolations)
                .flatMap(Collection::stream)
                .collect(groupingBy(EmployeeDateViolation::getDate));

        return Stream.of(swapShiftsInTheSameDaysBetweenEmployees(schedule, employeeDateViolationsByDate, adaptation), addWorkingShifts(schedule, employeeDateViolationsByDate, adaptation), removeShifts(schedule, employeeDateViolationsByDate, adaptation))
                .flatMap(identity())
                .distinct();
    }

}
