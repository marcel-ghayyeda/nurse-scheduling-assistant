package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee;

public class EmployeeShiftAssignmentBuilder {

    private Employee employee;
    private Shift shift;

    public static EmployeeShiftAssignmentBuilder employeeShiftAssignment(){
        return new EmployeeShiftAssignmentBuilder();
    }

    public EmployeeShiftAssignmentBuilder employee(Employee employee){
        this.employee = employee;
        return this;
    }


    public EmployeeShiftAssignmentBuilder shift(Shift shift){
        this.shift = shift;
        return this;
    }

    public EmployeeShiftAssignment build(){
        return new EmployeeShiftAssignment(employee, shift);
    }


}
