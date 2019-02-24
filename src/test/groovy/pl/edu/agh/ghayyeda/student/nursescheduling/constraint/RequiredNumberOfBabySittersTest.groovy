package pl.edu.agh.ghayyeda.student.nursescheduling.constraint

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

import static java.time.Month.NOVEMBER
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignmentBuilder.employeeShiftAssignment
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*

class RequiredNumberOfBabySittersTest extends Specification {

    def "Should classify as feasible when enough baby sitters during day and night"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), DAY.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 2), NIGHT.endTime)
        def constraint = RequiredNumberOfBabySitters.between(validationStartTime, validationEndTime, 4)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 2")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 3")).shift(NIGHT))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 4")).shift(NIGHT))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        constraintValidationResult.feasible
    }

    def "Should classify as not feasible when no babysitter at some time during day"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), DAY.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 2), NIGHT.endTime)
        def constraint = RequiredNumberOfBabySitters.between(validationStartTime, validationEndTime, 4)

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

    def "Should classify as not feasible when no baby sitters but nurses present"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), DAY.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 2), NIGHT.endTime)
        def constraint = RequiredNumberOfBabySitters.between(validationStartTime, validationEndTime, 4)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse2")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse3")).shift(NIGHT))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse4")).shift(NIGHT))
                .build()

        when:
        def constraintValidationResult = constraint.validate(schedule)

        then:
        !constraintValidationResult.feasible
    }


}
