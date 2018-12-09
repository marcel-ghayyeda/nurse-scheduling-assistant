package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import dnl.utils.text.table.TextTable;
import io.vavr.Lazy;
import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.Locale.US;
import static java.util.stream.Collectors.*;

public class Schedule {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d").localizedBy(US);

    private final List<DateEmployeeShiftAssignments> schedule;
    private final Lazy<String> stringTableSchedule;

    Schedule(List<DateEmployeeShiftAssignments> schedule) {
        this.schedule = schedule;
        this.stringTableSchedule = Lazy.of(this::buildHumanFriendlyString);
    }

    public Stream<EmployeeShiftAssignment> getEmployeeShiftAssignmentsFor(LocalDateTime localDateTime) {
        return schedule.stream()
                .flatMap(dateEmployeeShiftAssignments -> dateEmployeeShiftAssignments.getFor(localDateTime));
    }

    public String toHumanFriendlyString() {
        return stringTableSchedule.get();
    }

    private String buildHumanFriendlyString() {
        var baos = new ByteArrayOutputStream();
        var ps = new PrintStream(baos);
        var columnNames = Stream.concat(Stream.of(""), listScheduleDays()).toArray(String[]::new);
        final String[][] data = groupByDate().values().stream()
                .flatMap(Collection::stream)
                .collect(groupingBy(EmployeeShiftAssignment::getEmployee))
                .entrySet()
                .stream()
                .map(entry -> Stream.concat(Stream.of(entry.getKey().getName()), listShifts(entry)).toArray(String[]::new))
                .toArray(String[][]::new);
        new TextTable(columnNames, data).printTable(ps, 0);
        return baos.toString();
    }

    private Stream<String> listScheduleDays() {
        return schedule.stream()
                .distinct()
                .map(DateEmployeeShiftAssignments::getStartDate)
                .sorted()
                .map(localDate -> localDate.format(formatter));
    }

    private Map<LocalDate, List<EmployeeShiftAssignment>> groupByDate() {
        return this.schedule.stream().collect(groupingBy(DateEmployeeShiftAssignments::getStartDate, TreeMap::new, flatMapping(x -> x.getShiftAssignments().stream(), toList())));
    }

    private Stream<String> listShifts(Map.Entry<Employee, List<EmployeeShiftAssignment>> entry) {
        return entry.getValue().stream().map(EmployeeShiftAssignment::getShift).map(Enum::toString);
    }
}
