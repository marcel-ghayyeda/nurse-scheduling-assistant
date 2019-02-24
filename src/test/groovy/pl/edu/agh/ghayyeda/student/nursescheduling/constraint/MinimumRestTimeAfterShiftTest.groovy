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
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(MORNING))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(NIGHT))

                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        !constraintValidationResult.feasible
    }

    def "Should classify as not feasible when first shift overlapses on the next day"() {
        given:
        def constraint = new MinimumRestTimeAfterShift()

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
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
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(MORNING))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_NIGHT))

                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY_NIGHT))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY_OFF))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY_NIGHT))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        constraintValidationResult.feasible
    }

}
