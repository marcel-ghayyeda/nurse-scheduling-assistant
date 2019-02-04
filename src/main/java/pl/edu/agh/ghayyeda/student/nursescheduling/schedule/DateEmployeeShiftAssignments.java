package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.W;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.randomWorkShift;
import static pl.edu.agh.ghayyeda.student.nursescheduling.util.Predicates.not;

public class DateEmployeeShiftAssignments {

    private final LocalDate startDate;
    private final List<EmployeeShiftAssignment> shiftAssignments;

    public DateEmployeeShiftAssignments(LocalDate startDate, List<EmployeeShiftAssignment> shiftAssignments) {
        this.startDate = startDate;
        this.shiftAssignments = shiftAssignments;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public boolean anyMatch(Predicate<EmployeeShiftAssignment> predicate) {
        return getShiftAssignments().stream().anyMatch(predicate);
    }

    public List<EmployeeShiftAssignment> getShiftAssignments() {
        return shiftAssignments;
    }

    public Stream<EmployeeShiftAssignment> getFor(LocalDateTime localDateTime) {
        return shiftAssignments.stream()
                .filter(lastsIn(localDateTime));
    }

    private Predicate<EmployeeShiftAssignment> lastsIn(LocalDateTime localDateTime) {
        return shiftAssignment -> {
            var shiftStartDateTime = LocalDateTime.of(startDate, shiftAssignment.getStartTime());
            return (shiftStartDateTime.isBefore(localDateTime) || shiftStartDateTime.equals(localDateTime)) && shiftStartDateTime.plus(shiftAssignment.getDuration()).isAfter(localDateTime);
        };
    }

    DateEmployeeShiftAssignments addRandomShift() {
        EmployeeShiftAssignment randomEmployeeShiftAssignmentwithFreeShift = getRandomEmployeeShiftAssignmentMatching(not(EmployeeShiftAssignment::isWorkDay), shiftAssignments);
        var randomEmployeeShiftAssignmentIndex = shiftAssignments.indexOf(randomEmployeeShiftAssignmentwithFreeShift);

        ArrayList<EmployeeShiftAssignment> newShiftAssignments = new ArrayList<>(shiftAssignments);
        newShiftAssignments.set(randomEmployeeShiftAssignmentIndex, randomEmployeeShiftAssignmentwithFreeShift.withShift(randomWorkShift()));

        return new DateEmployeeShiftAssignments(startDate, newShiftAssignments);
    }

    DateEmployeeShiftAssignments removeRandomShift() {
        EmployeeShiftAssignment randomEmployeeShiftAssignmentwithWorkingShift = getRandomEmployeeShiftAssignmentMatching(EmployeeShiftAssignment::isWorkDay, shiftAssignments);
        var randomEmployeeShiftAssignmentIndex = shiftAssignments.indexOf(randomEmployeeShiftAssignmentwithWorkingShift);

        ArrayList<EmployeeShiftAssignment> newShiftAssignments = new ArrayList<>(shiftAssignments);
        newShiftAssignments.set(randomEmployeeShiftAssignmentIndex, randomEmployeeShiftAssignmentwithWorkingShift.withShift(W));

        return new DateEmployeeShiftAssignments(startDate, newShiftAssignments);
    }

    private EmployeeShiftAssignment getRandomEmployeeShiftAssignmentMatching(Predicate<EmployeeShiftAssignment> predicate, Collection<EmployeeShiftAssignment> employeeShiftAssignments) {
        var employeeShiftAssignmentsMatchingPredicate = employeeShiftAssignments.stream()
                .filter(predicate)
                .collect(toList());
        return employeeShiftAssignmentsMatchingPredicate.get(ThreadLocalRandom.current().nextInt(employeeShiftAssignmentsMatchingPredicate.size()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateEmployeeShiftAssignments that = (DateEmployeeShiftAssignments) o;
        return Objects.equals(startDate, that.startDate) &&
                Objects.equals(shiftAssignments, that.shiftAssignments);
    }

    @Override
    public int hashCode() {

        return Objects.hash(startDate, shiftAssignments);
    }
}
