package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class Schedule {


    private final List<DateEmployeeShiftAssignments> schedule;

    Schedule(List<DateEmployeeShiftAssignments> schedule) {
        this.schedule = schedule;
    }

    public List<DateEmployeeShiftAssignments> getDateEmployeeShiftAssignmentsByDate() {
        return schedule;
    }

    public Stream<EmployeeShiftAssignment> getEmployeeShiftAssignmentsFor(LocalDateTime localDateTime) {
        return schedule.stream()
                .flatMap(dateEmployeeShiftAssignments -> dateEmployeeShiftAssignments.getFor(localDateTime));
    }

    public Stream<DateEmployeeShiftAssignment> getDateShiftAssignments() {
        return schedule.stream()
                .flatMap(dateEmployeeShiftAssignments -> dateEmployeeShiftAssignments.getShiftAssignments().stream().map(employeeShiftAssignment -> new DateEmployeeShiftAssignment(dateEmployeeShiftAssignments.getStartDate(), employeeShiftAssignment)));
    }

}
