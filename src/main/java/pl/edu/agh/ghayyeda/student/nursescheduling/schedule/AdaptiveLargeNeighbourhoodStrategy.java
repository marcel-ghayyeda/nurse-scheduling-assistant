package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintViolationsDescription;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.EmployeeDateViolation;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationResult;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class AdaptiveLargeNeighbourhoodStrategy extends AbstractNeighbourhoodStrategy implements NeighbourhoodStrategy {

    private static final Logger log = LoggerFactory.getLogger(AdaptiveLargeNeighbourhoodStrategy.class);

    @Override
    public Neighbourhood createNeighbourhood(Schedule schedule, ScheduleConstraintValidationResult constraintValidationResult) {
        var employeeDateViolationsByDate = constraintValidationResult.getConstraintViolationsDescriptions().stream()
                .map(ConstraintViolationsDescription::getEmployeeDateViolations)
                .flatMap(Collection::stream)
                .collect(groupingBy(EmployeeDateViolation::getDate));
        var neighbourhood = Stream.concat(addWorkingShifts(schedule, employeeDateViolationsByDate), removeShifts(schedule, employeeDateViolationsByDate)).collect(toList());
        log.debug("Neighbourhood size: {}", neighbourhood.size());
        return new Neighbourhood(neighbourhood);
    }

    private Stream<Schedule> addWorkingShifts(Schedule schedule, Map<LocalDate, List<EmployeeDateViolation>> employeeDateViolationsByDate) {
        return schedule.getDateShiftAssignmentMatching(DateEmployeeShiftAssignment::isDayOff)
                .filter(isEligibleForChanging(employeeDateViolationsByDate))
                .flatMap(createNeighboursWithAllWorkingShifts(schedule));
    }

    private Stream<Schedule> removeShifts(Schedule schedule, Map<LocalDate, List<EmployeeDateViolation>> employeeDateViolationsByDate) {
        return schedule.getDateShiftAssignmentMatching(DateEmployeeShiftAssignment::isWorkDay)
                .filter(isEligibleForChanging(employeeDateViolationsByDate))
                .map(createNeighbourWithDayOffShift(schedule));
    }

    private Predicate<DateEmployeeShiftAssignment> isEligibleForChanging(Map<LocalDate, List<EmployeeDateViolation>> byDate) {
        return dateEmployeeShiftAssignment -> byDate.get(dateEmployeeShiftAssignment.getStartDate()) != null && byDate.get(dateEmployeeShiftAssignment.getStartDate()).stream().anyMatch(refersTo(dateEmployeeShiftAssignment));
    }

    private Predicate<EmployeeDateViolation> refersTo(DateEmployeeShiftAssignment dateEmployeeShiftAssignment) {
        return employeeDateViolation -> !employeeDateViolation.getEmployee().isPresent() || employeeDateViolation.getEmployee().get().equals(dateEmployeeShiftAssignment.getEmployee());
    }

}
