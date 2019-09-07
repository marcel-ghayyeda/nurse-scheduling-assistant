package pl.edu.agh.ghayyeda.student.nursescheduling.constraint.failfast

import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Employee
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

import static java.time.Month.NOVEMBER
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignmentBuilder.employeeShiftAssignment
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*

class FailFastRequiredNumberOfEmployeesTest extends Specification {

    def "Should classify as not feasible when no nurse in some hour of day"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), DAY.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 2), NIGHT.endTime)
        def constraint = FailFastRequiredNumberOfEmployees.between(validationStartTime, validationEndTime, 1)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(MORNING))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
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
        def constraint = FailFastRequiredNumberOfEmployees.between(validationStartTime, validationEndTime, 1)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        !constraintValidationResult.feasible
    }

    def "Should classify as not feasible when no nurse in some hour of day but babysitters present"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), DAY.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 2), NIGHT.endTime)
        def constraint = FailFastRequiredNumberOfEmployees.between(validationStartTime, validationEndTime, 1)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("BabySitter 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("BabySitter 2")).shift(NIGHT))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        !constraintValidationResult.feasible
    }


    def "Should classify as feasible when one nurse always present"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), DAY.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 3), NIGHT.endTime)
        def constraint = FailFastRequiredNumberOfEmployees.between(validationStartTime, validationEndTime, 1)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        constraintValidationResult.feasible
    }

    def "Should classify as feasible when more than one nurse always present"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), DAY.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 2), NIGHT.endTime)
        def constraint = FailFastRequiredNumberOfEmployees.between(validationStartTime, validationEndTime, 1)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 3")).shift(MORNING))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 4")).shift(NIGHT))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 5")).shift(NIGHT))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        constraintValidationResult.feasible
    }

    def "Should classify as feasible when enough employees during day and night"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), DAY.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 2), NIGHT.endTime)
        def constraint = FailFastRequiredNumberOfEmployees.between(validationStartTime, validationEndTime, 4)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Baby sitter 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 2")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(NIGHT))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Nurse 2")).shift(NIGHT))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        constraintValidationResult.feasible
    }

    def "Should classify as not feasible when no employee at some time during day"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), DAY.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 2), NIGHT.endTime)
        def constraint = FailFastRequiredNumberOfEmployees.between(validationStartTime, validationEndTime, 4)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 1")).shift(MORNING))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 2")).shift(MORNING))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 3")).shift(NIGHT))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 4")).shift(NIGHT))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        !constraintValidationResult.feasible
    }

}
