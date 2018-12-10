package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import dnl.utils.text.table.TextTable;
import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.Locale.US;
import static java.util.stream.Collectors.*;

public class ScheduleAsciiTablePresenter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d").localizedBy(US);

    public String buildAsciiTableRepresentationOf(Schedule schedule) {
        var baos = new ByteArrayOutputStream();
        var ps = new PrintStream(baos);
        var columnNames = Stream.concat(Stream.of(""), listScheduleDays(schedule)).toArray(String[]::new);
        final String[][] data = groupByDate(schedule).values().stream()
                .flatMap(Collection::stream)
                .collect(groupingBy(EmployeeShiftAssignment::getEmployee))
                .entrySet()
                .stream()
                .map(entry -> Stream.concat(Stream.of(entry.getKey().getName()), listShifts(entry)).toArray(String[]::new))
                .toArray(String[][]::new);
        new TextTable(columnNames, data).printTable(ps, 0);
        return baos.toString();
    }

    private Stream<String> listScheduleDays(Schedule schedule) {
        return schedule.getDateEmployeeShiftAssignments().stream()
                .distinct()
                .map(DateEmployeeShiftAssignments::getStartDate)
                .sorted()
                .map(localDate -> localDate.format(formatter));
    }


    private Map<LocalDate, List<EmployeeShiftAssignment>> groupByDate(Schedule schedule) {
        return schedule.getDateEmployeeShiftAssignments().stream().collect(groupingBy(DateEmployeeShiftAssignments::getStartDate, TreeMap::new, flatMapping(x -> x.getShiftAssignments().stream(), toList())));
    }

    private Stream<String> listShifts(Map.Entry<Employee, List<EmployeeShiftAssignment>> entry) {
        return entry.getValue().stream().map(EmployeeShiftAssignment::getShift).map(Enum::toString);
    }
}
