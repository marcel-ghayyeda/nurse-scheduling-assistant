package pl.edu.agh.ghayyeda.student.nursescheduling.constraint

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

import static java.time.Month.NOVEMBER
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignmentBuilder.employeeShiftAssignment
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*

class AlwaysAtLeastOneNurseTest extends Specification {

    def "Should classify as not feasible when no nurse in some hour of day"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), D.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 2), N.endTime)
        def constraint = AlwaysAtLeastOneNurse.between(validationStartTime, validationEndTime)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(R))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        !constraintValidationResult.feasible
    }

    def "Should classify as not feasible when nurse has a day off"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), LocalTime.of(7, 0))
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 2), LocalTime.of(12, 0))
        def constraint = AlwaysAtLeastOneNurse.between(validationStartTime, validationEndTime)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(W))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        !constraintValidationResult.feasible
    }

    def "Should classify as not feasible when no nurse in some hour of day but babysitters present"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), D.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 2), N.endTime)
        def constraint = AlwaysAtLeastOneNurse.between(validationStartTime, validationEndTime)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("BabySitter 1")).shift(D))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("BabySitter 2")).shift(N))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        !constraintValidationResult.feasible
    }


    def "Should classify as feasible when one nurse always present"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), D.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 3), N.endTime)
        def constraint = AlwaysAtLeastOneNurse.between(validationStartTime, validationEndTime)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        constraintValidationResult.feasible
    }

    def "Should classify as feasible when more than one nurse always present"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), D.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 2), N.endTime)
        def constraint = AlwaysAtLeastOneNurse.between(validationStartTime, validationEndTime)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(D))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 3")).shift(R))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 4")).shift(N))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 5")).shift(N))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        constraintValidationResult.feasible
    }

}
