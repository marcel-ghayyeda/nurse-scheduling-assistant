package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public final class EmployeeDateViolation {
    private Employee employee;
    private LocalDate date;

    public EmployeeDateViolation(Employee employee, LocalDate date) {
        this.employee = employee;
        this.date = date;
    }

    public EmployeeDateViolation(LocalDate date) {
        this.employee = null;
        this.date = date;
    }

    public Optional<Employee> getEmployee() {
        return Optional.ofNullable(employee);
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeDateViolation that = (EmployeeDateViolation) o;
        return Objects.equals(employee, that.employee) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {

        return Objects.hash(employee, date);
    }
}
