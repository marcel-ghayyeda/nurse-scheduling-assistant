package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.time.LocalDate;
import java.util.Map;

public interface EmployeeShiftMap {

    Employee getEmployee();
    Map<LocalDate, Shift> getDateShiftMap();
}
