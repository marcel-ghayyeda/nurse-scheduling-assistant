package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class AvailabilityPerEmployee {

    private final Map<String, EmployeeAvailability> availabilityPerEmployee;

    private AvailabilityPerEmployee(Map<String, EmployeeAvailability> availabilityPerEmployee) {
        this.availabilityPerEmployee = availabilityPerEmployee;
    }

    static AvailabilityPerEmployee empty() {
        return new AvailabilityPerEmployee(new HashMap<>());
    }

    public EmployeeAvailability getAvailabilityFor(Employee employee) {
        return ofNullable(availabilityPerEmployee.get(employee.getId())).orElse(EmployeeAvailability.FULL_TIME);
    }

    public void set(Employee employee, EmployeeAvailability availability) {
        availabilityPerEmployee.put(employee.getId(), availability);
    }
}
