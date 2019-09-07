package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.time.LocalDate;
import java.util.Map;

public interface EmployeeShiftMap {

    Employee getEmployee();
    Map<LocalDate, Shift> getDateShiftMap();
}
