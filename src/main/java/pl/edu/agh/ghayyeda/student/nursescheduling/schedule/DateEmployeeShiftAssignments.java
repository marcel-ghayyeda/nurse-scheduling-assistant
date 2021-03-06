package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DateEmployeeShiftAssignments {

    private final LocalDate startDate;
    private final Collection<EmployeeShiftAssignment> shiftAssignments;

    public DateEmployeeShiftAssignments(LocalDate startDate, Collection<EmployeeShiftAssignment> shiftAssignments) {
        this.startDate = startDate;
        this.shiftAssignments = shiftAssignments;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Collection<EmployeeShiftAssignment> getShiftAssignments() {
        return shiftAssignments;
    }

    public Stream<EmployeeShiftAssignment> getFor(LocalDateTime localDateTime) {
        return shiftAssignments.stream().filter(lastsIn(localDateTime));
    }

    private Predicate<EmployeeShiftAssignment> lastsIn(LocalDateTime localDateTime) {
        return shiftAssignment -> {
            var shiftStartDateTime = LocalDateTime.of(startDate, shiftAssignment.getStartTime());
            return (!shiftStartDateTime.isAfter(localDateTime)) && shiftStartDateTime.plus(shiftAssignment.getDuration()).isAfter(localDateTime);
        };
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
