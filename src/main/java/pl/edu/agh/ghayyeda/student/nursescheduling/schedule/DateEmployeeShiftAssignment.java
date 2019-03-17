package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

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

    public LocalDate getEndDate() {
        return employeeShiftAssignment.getShift().endsOnNextDay() ? getStartDate().plusDays(1) : getStartDate();
    }

    public EmployeeShiftAssignment getEmployeeShiftAssignment() {
        return employeeShiftAssignment;
    }

    public Employee getEmployee() {
        return employeeShiftAssignment.getEmployee();
    }

    public Shift getShift() {
        return employeeShiftAssignment.getShift();
    }

    public boolean isWorkDay() {
        return getShift().isWorkDay();
    }

    public boolean isDayOff() {
        return getShift().isDayOff();
    }

    DateEmployeeShiftAssignment setShift(Shift shift) {
        return new DateEmployeeShiftAssignment(getStartDate(), new EmployeeShiftAssignment(getEmployee(), shift));
    }

    DateEmployeeShiftAssignment removeShift() {
        return new DateEmployeeShiftAssignment(getStartDate(), new EmployeeShiftAssignment(getEmployee(), Shift.DAY_OFF));
    }

    public Duration getShiftDuration() {
        return getShift().getDuration();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateEmployeeShiftAssignment that = (DateEmployeeShiftAssignment) o;
        return Objects.equals(startDate, that.startDate) &&
                Objects.equals(employeeShiftAssignment, that.employeeShiftAssignment);
    }

    @Override
    public int hashCode() {

        return Objects.hash(startDate, employeeShiftAssignment);
    }
}
