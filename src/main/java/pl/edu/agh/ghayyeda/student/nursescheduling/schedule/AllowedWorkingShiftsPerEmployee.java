package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Optional.ofNullable;

public class AllowedWorkingShiftsPerEmployee {
    private final Map<String, Collection<Shift>> allowedWorkingShiftPerEmployee;

    private AllowedWorkingShiftsPerEmployee(Map<String, Collection<Shift>> allowedWorkingShiftPerEmployee) {
        this.allowedWorkingShiftPerEmployee = allowedWorkingShiftPerEmployee;
    }

    static AllowedWorkingShiftsPerEmployee empty() {
        return new AllowedWorkingShiftsPerEmployee(new HashMap<>());
    }

    public Collection<Shift> getAllowedWorkingShiftsFor(Employee employee) {
        return ofNullable(allowedWorkingShiftPerEmployee.get(employee.getId())).orElse(Set.of(Shift.DAY, Shift.NIGHT, Shift.DAY_NIGHT));
    }

    public void set(Employee employee, Set<Shift> allowedShifts) {
        allowedWorkingShiftPerEmployee.put(employee.getId(), allowedShifts);
    }
}
