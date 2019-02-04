package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

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

    EmployeeShiftAssignment withShift(Shift shift) {
        return new EmployeeShiftAssignment(employee, shift);
    }

    boolean isWorkDay() {
        return shift.isWorkDay();
    }

    Duration getDuration() {
        return shift.getDuration();
    }

    LocalTime getStartTime() {
        return shift.getStartTime();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeShiftAssignment that = (EmployeeShiftAssignment) o;
        return Objects.equals(employee, that.employee) &&
                shift == that.shift;
    }

    @Override
    public int hashCode() {

        return Objects.hash(employee, shift);
    }
}
