package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import java.util.Collection;
import java.util.Objects;

public class ConstraintViolationsDescription {

    private final String description;
    private final Collection<EmployeeDateViolation> employeeDateViolations;

    public ConstraintViolationsDescription(String description, Collection<EmployeeDateViolation> employeeDateViolations) {
        this.description = description;
        this.employeeDateViolations = employeeDateViolations;
    }

    public String getDescription() {
        return description;
    }

    public Collection<EmployeeDateViolation> getEmployeeDateViolations() {
        return employeeDateViolations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstraintViolationsDescription that = (ConstraintViolationsDescription) o;
        return Objects.equals(description, that.description) &&
                Objects.equals(employeeDateViolations, that.employeeDateViolations);
    }

    @Override
    public int hashCode() {

        return Objects.hash(description, employeeDateViolations);
    }
}
