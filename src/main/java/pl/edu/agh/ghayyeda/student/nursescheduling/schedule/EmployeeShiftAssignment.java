package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

public class EmployeeShiftAssignment {

    private final Employee employee;
    private final Shift shift;

    public EmployeeShiftAssignment(Employee employee, Shift shift) {
        this.employee = employee;
        this.shift = shift;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Shift getShift() {
        return shift;
    }
}
