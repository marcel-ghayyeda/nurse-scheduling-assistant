package pl.edu.agh.ghayyeda.student.nursescheduling.solver

import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationFacade
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.StaticScheduleConstraintFactory
import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

import static java.time.Month.NOVEMBER
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignmentBuilder.employeeShiftAssignment
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*

class BfsSolverTest extends Specification {

    def scheduleConstraintValidationFacade

    def setup() {
        scheduleConstraintValidationFacade = new ScheduleConstraintValidationFacade(new StaticScheduleConstraintFactory())
    }

    def "Should return the same feasible schedule"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), DAY.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 3), NIGHT.endTime)
        def solver = new BfsSolver(scheduleConstraintValidationFacade, validationStartTime, validationEndTime, 3)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 2")).shift(NIGHT))
                .build()

        when:
        def foundSchedule = solver.findFeasibleSchedule(schedule)

        then:
        foundSchedule == schedule
    }


    def "Should find feasible schedule when only one change needed"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), DAY.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 3), NIGHT.endTime)
        def solver = new BfsSolver(scheduleConstraintValidationFacade, validationStartTime, validationEndTime, 3)

        def originalSchedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 2")).shift(NIGHT))
                .build()

        when:
        def foundSchedule = solver.findFeasibleSchedule(originalSchedule)

        then:
        def expectedSchedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.babySitter("Baby sitter 2")).shift(NIGHT))
                .build()

        foundSchedule == expectedSchedule
    }


}
