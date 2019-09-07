package pl.edu.agh.ghayyeda.student.nursescheduling.schedule

import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintViolationsDescription
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.EmployeeDateViolation
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.neighbourhood.AdaptiveLargeNeighbourhoodStrategy
import spock.lang.Specification

import java.time.LocalDate

import static java.time.Month.NOVEMBER
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.neighbourhood.Adaptation.NARROW
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.neighbourhood.Adaptation.WIDE
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignmentBuilder.employeeShiftAssignment
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*

class AdaptiveLargeNeighbourhoodStrategyTest extends Specification {

    def "Should create neighbours by changing only those shifts which cause a validation problem"() {
        given:
        def neighbourhoodStrategy = new AdaptiveLargeNeighbourhoodStrategy(NARROW)
        def originalSchedule = baseScheduleBuilder().build()

        def employeeDateViolation1 = new EmployeeDateViolation(Employee.nurse("Nurse 1"), LocalDate.of(2018, NOVEMBER, 1))
        def employeeDateViolation2 = new EmployeeDateViolation(LocalDate.of(2018, NOVEMBER, 2))
        def constraintViolationsDescription = new ConstraintViolationsDescription("", [employeeDateViolation1, employeeDateViolation2])
        def validationResult = ConstraintValidationResult.ofPenalty(0.002, [constraintViolationsDescription])

        when:
        def newSchedules = neighbourhoodStrategy.createNeighbourhood(originalSchedule, validationResult).getSchedules()

        then:
        newSchedules.size() == 3
        newSchedules.contains(baseScheduleBuilder()
                .removeDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
                .build())

        newSchedules.contains(baseScheduleBuilder()
                .removeDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
                .build())

        newSchedules.contains(baseScheduleBuilder()
                .removeDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY_OFF))
                .build())
    }

    def "Should create neighbours by changing only shifts near those which cause a validation problem for employee-based validation"() {
        given:
        def neighbourhoodStrategy = new AdaptiveLargeNeighbourhoodStrategy(WIDE)
        def originalSchedule = baseScheduleBuilder().build()

        def employeeDateViolation1 = new EmployeeDateViolation(Employee.nurse("Nurse 1"), LocalDate.of(2018, NOVEMBER, 2))
        def constraintViolationsDescription = new ConstraintViolationsDescription("", [employeeDateViolation1])
        def validationResult = ConstraintValidationResult.ofPenalty(0.002, [constraintViolationsDescription])

        when:
        def newSchedules = neighbourhoodStrategy.createNeighbourhood(originalSchedule, validationResult).getSchedules()

        then:
        newSchedules.size() == 3
        newSchedules.contains(baseScheduleBuilder()
                .removeDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
                .build())

        newSchedules.contains(baseScheduleBuilder()
                .removeDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
                .build())

        newSchedules.contains(baseScheduleBuilder()
                .removeDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
                .build())
    }

    def "Should create neighbours by changing only shifts near those which cause a validation problem for date-based validation"() {
        given:
        def neighbourhoodStrategy = new AdaptiveLargeNeighbourhoodStrategy(WIDE)
        def originalSchedule = baseScheduleBuilder().build()

        def employeeDateViolation1 = new EmployeeDateViolation(LocalDate.of(2018, NOVEMBER, 2))
        def constraintViolationsDescription = new ConstraintViolationsDescription("", [employeeDateViolation1])
        def validationResult = ConstraintValidationResult.ofPenalty(0.002, [constraintViolationsDescription])

        when:
        def newSchedules = neighbourhoodStrategy.createNeighbourhood(originalSchedule, validationResult).getSchedules()

        then:
        newSchedules.size() == 6
        newSchedules.contains(baseScheduleBuilder()
                .removeDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
                .build())

        newSchedules.contains(baseScheduleBuilder()
                .removeDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY_OFF))
                .build())

        newSchedules.contains(baseScheduleBuilder()
                .removeDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
                .build())

        newSchedules.contains(baseScheduleBuilder()
                .removeDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY_OFF))
                .build())

        newSchedules.contains(baseScheduleBuilder()
                .removeDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
                .build())

        newSchedules.contains(baseScheduleBuilder()
                .removeDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY_OFF))
                .build())
    }


    private static ScheduleBuilder baseScheduleBuilder() {
        schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY))

                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))

                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))

                .onDay(4, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(4, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))

                .onDay(5, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(5, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
    }

}
