package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.time.Duration;
import java.time.LocalTime;

public class EmployeeShiftAssignment {

    private final Employee employee;
    private final Shift shift;

    EmployeeShiftAssignment(Employee employee, Shift shift) {
        this.employee = employee;
        this.shift = shift;
    }

    Employee getEmployee() {
        return employee;
    }

    public Employee.Type getEmployeeType() {
        return employee.getType();
    }

    Shift getShift() {
        return shift;
    }

    Duration getDuration() {
        return shift.getDuration();
    }

    LocalTime getStartTime() {
        return shift.getStartTime();
    }
}
