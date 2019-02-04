package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static pl.edu.agh.ghayyeda.student.nursescheduling.util.Predicates.not;

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

    public List<Schedule> getNeighbourhood() {
        return List.of(addRandomShift(), removeRandomShift());
    }

    Schedule addRandomShift() {
        DateEmployeeShiftAssignments randomDateEmployeeShiftAssignments = getRandomDateShiftAssignmentsMatching(dateEmployeeShiftAssignments -> dateEmployeeShiftAssignments.anyMatch(not(EmployeeShiftAssignment::isWorkDay)), schedule);
        DateEmployeeShiftAssignments dateEmployeeShiftAssignmentsWithAddedShift = randomDateEmployeeShiftAssignments.addRandomShift();

        List<DateEmployeeShiftAssignments> newSchedule = new ArrayList<>(schedule);
        newSchedule.set(schedule.indexOf(randomDateEmployeeShiftAssignments), dateEmployeeShiftAssignmentsWithAddedShift);
        return new Schedule(newSchedule);
    }

    Schedule removeRandomShift() {
        DateEmployeeShiftAssignments randomDateEmployeeShiftAssignments = getRandomDateShiftAssignmentsMatching(dateEmployeeShiftAssignments -> dateEmployeeShiftAssignments.anyMatch(EmployeeShiftAssignment::isWorkDay), schedule);
        DateEmployeeShiftAssignments dateEmployeeShiftAssignmentsWithAddedShift = randomDateEmployeeShiftAssignments.removeRandomShift();

        List<DateEmployeeShiftAssignments> newSchedule = new ArrayList<>(schedule);
        newSchedule.set(schedule.indexOf(randomDateEmployeeShiftAssignments), dateEmployeeShiftAssignmentsWithAddedShift);
        return new Schedule(newSchedule);
    }

    private DateEmployeeShiftAssignments getRandomDateShiftAssignmentsMatching(Predicate<DateEmployeeShiftAssignments> predicate, Collection<DateEmployeeShiftAssignments> dateEmployeeShiftAssignments) {
        var dateShiftAssignmentsMatchingPredicate = dateEmployeeShiftAssignments.stream()
                .filter(predicate)
                .collect(toList());

        return dateShiftAssignmentsMatchingPredicate.get(ThreadLocalRandom.current().nextInt(dateShiftAssignmentsMatchingPredicate.size()));
    }
}
