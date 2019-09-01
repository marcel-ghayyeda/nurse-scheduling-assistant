package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintViolationsDescription;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.EmployeeDateViolation;
import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class AdaptiveLargeNeighbourhoodStrategy extends AbstractNeighbourhoodStrategy implements NeighbourhoodStrategy {

    public enum Adaptation {
        NARROW,
        WIDE
    }

    private final Adaptation adaptation;

    public AdaptiveLargeNeighbourhoodStrategy(Adaptation adaptation) {
        this.adaptation = adaptation;
    }

    private static final Logger log = LoggerFactory.getLogger(AdaptiveLargeNeighbourhoodStrategy.class);

    @Override
    public Neighbourhood createNeighbourhood(Schedule schedule, ConstraintValidationResult constraintValidationResult) {
        var employeeDateViolationsByDate = constraintValidationResult.getConstraintViolationsDescriptions().stream()
                .map(ConstraintViolationsDescription::getEmployeeDateViolations)
                .flatMap(Collection::stream)
                .collect(groupingBy(EmployeeDateViolation::getDate));
        var neighbourhood = Stream.of(swapShiftsInTheSameDaysBetweenEmployees(schedule, employeeDateViolationsByDate), addWorkingShifts(schedule, employeeDateViolationsByDate), removeShifts(schedule, employeeDateViolationsByDate))
                .flatMap(identity())
                .collect(toList());
        log.debug("Neighbourhood size: {}", neighbourhood.size());
        return new Neighbourhood(neighbourhood);
    }

    private Stream<Schedule> swapShiftsInTheSameDaysBetweenEmployees(Schedule schedule, Map<LocalDate, List<EmployeeDateViolation>> employeeDateViolationsByDate) {
        return schedule.getDateShiftAssignmentMatching(this::isWorkDayOrDayOff)
                .filter(isEligibleForChanging(employeeDateViolationsByDate))
                .flatMap(shiftAssignment1 ->
                        schedule.getDateShiftAssignmentMatching(isEligibleToSwap(shiftAssignment1))
                                .filter(shiftAssignment2 -> schedule.isAllowedShift(shiftAssignment2.getEmployee(), shiftAssignment1.getShift()))
                                .map(shiftAssignment2 -> createNeighbourWithSwappedShifts(schedule, shiftAssignment1, shiftAssignment2)));
    }


    Function<DateEmployeeShiftAssignment, Stream<? extends Schedule>> createNeighboursWithAllWorkingShifts(Schedule schedule) {
        return dateEmployeeShiftAssignment -> schedule.getAllowedWorkingShiftsFor(dateEmployeeShiftAssignment.getEmployee()).stream().map(shift -> createNeighbour(schedule, dateEmployeeShiftAssignment, shift));
    }

    private Stream<Schedule> addWorkingShifts(Schedule schedule, Map<LocalDate, List<EmployeeDateViolation>> employeeDateViolationsByDate) {
        return schedule.getDateShiftAssignmentMatching(this::isWorkDayOrDayOff)
                .filter(isEligibleForChanging(employeeDateViolationsByDate))
                .flatMap(createNeighboursWithAllWorkingShifts(schedule));
    }

    private Stream<Schedule> removeShifts(Schedule schedule, Map<LocalDate, List<EmployeeDateViolation>> employeeDateViolationsByDate) {
        return schedule.getDateShiftAssignmentMatching(DateEmployeeShiftAssignment::isWorkDay)
                .filter(isEligibleForChanging(employeeDateViolationsByDate))
                .map(createNeighbourWithDayOffShift(schedule));
    }

    private Predicate<DateEmployeeShiftAssignment> isEligibleForChanging(Map<LocalDate, List<EmployeeDateViolation>> violationsByDate) {
        return dateEmployeeShiftAssignment -> getViolations(violationsByDate, dateEmployeeShiftAssignment)
                .anyMatch(refersTo(dateEmployeeShiftAssignment.getEmployee()));
    }

    private Stream<EmployeeDateViolation> getViolations(Map<LocalDate, List<EmployeeDateViolation>> violationsByDate, DateEmployeeShiftAssignment dateEmployeeShiftAssignment) {
        switch (adaptation) {
            case NARROW:
                return violationsByDate.getOrDefault(dateEmployeeShiftAssignment.getStartDate(), List.of()).stream();
            case WIDE:
                return Stream.of(
                        violationsByDate.getOrDefault(dateEmployeeShiftAssignment.getStartDate(), List.of()),
                        violationsByDate.getOrDefault(dateEmployeeShiftAssignment.getStartDate().minusDays(1), List.of()),
                        violationsByDate.getOrDefault(dateEmployeeShiftAssignment.getStartDate().plusDays(1), List.of())

                ).flatMap(Collection::stream);
            default:
                throw new IllegalStateException();
        }
    }

    private Predicate<EmployeeDateViolation> refersTo(Employee employee) {
        return employeeDateViolation -> employeeDateViolation.getEmployee().map(employee::equals).orElse(true);
    }

}
