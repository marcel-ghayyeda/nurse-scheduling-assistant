package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.time.LocalDate;

public class DateEmployeeShiftAssignment {

    private final LocalDate startDate;
    private final EmployeeShiftAssignment employeeShiftAssignment;

    public DateEmployeeShiftAssignment(LocalDate startDate, EmployeeShiftAssignment employeeShiftAssignment) {
        this.startDate = startDate;
        this.employeeShiftAssignment = employeeShiftAssignment;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Employee getEmployee(){
        return employeeShiftAssignment.getEmployee();
    }

    public Shift getShift(){
        return employeeShiftAssignment.getShift();
    }

    public boolean isWorkDay(){
        return getShift().isWorkDay();
    }
}
