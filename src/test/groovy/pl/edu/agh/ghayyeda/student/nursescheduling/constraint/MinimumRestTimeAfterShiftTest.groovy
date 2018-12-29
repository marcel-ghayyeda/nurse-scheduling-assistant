package pl.edu.agh.ghayyeda.student.nursescheduling.constraint

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee
import spock.lang.Specification

import static java.time.Month.NOVEMBER
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignmentBuilder.employeeShiftAssignment
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*

class MinimumRestTimeAfterShiftTest extends Specification {

    def "Should classify as not feasible when one nurse has night shift and morning shift on the same day"() {
        given:
        def constraint = new MinimumRestTimeAfterShift()

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(R))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(N))

                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        !constraintValidationResult.feasible
    }

    def "Should classify as feasible when all nurses have minimum required rest time assured"() {
        given:
        def constraint = new MinimumRestTimeAfterShift()

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(R))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DN))

                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DN))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(W))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DN))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        constraintValidationResult.feasible
    }

}
