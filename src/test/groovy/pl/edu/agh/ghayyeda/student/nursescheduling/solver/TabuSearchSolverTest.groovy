package pl.edu.agh.ghayyeda.student.nursescheduling.solver

import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.penaltyaware.PenaltyAwareScheduleConstraintFactory
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.penaltyaware.PenaltyAwareScheduleConstraintValidationFacade
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.neighbourhood.NeighbourhoodStrategyFactory
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleAsciiTablePresenter
import pl.edu.agh.ghayyeda.student.nursescheduling.util.ScheduleValidationUtils
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

import static java.time.Month.NOVEMBER
import static java.time.Month.SEPTEMBER
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignmentBuilder.employeeShiftAssignment
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*
import static pl.edu.agh.ghayyeda.student.nursescheduling.solver.SolverAccuracy.NONE
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Employee.babySitter
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Employee.nurse

class TabuSearchSolverTest extends Specification {

    def scheduleConstraintValidationFacade
    def neighbourhoodStrategyFactory

    def setup() {
        scheduleConstraintValidationFacade = new PenaltyAwareScheduleConstraintValidationFacade(new PenaltyAwareScheduleConstraintFactory())
        neighbourhoodStrategyFactory = new NeighbourhoodStrategyFactory();
    }

    def "Should return the same feasible schedule"() {
        given:
        def validationStartTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 1), DAY.startTime)
        def validationEndTime = LocalDateTime.of(LocalDate.of(2018, NOVEMBER, 3), NIGHT.endTime)
        def solver = new TabuSearchSolver(neighbourhoodStrategyFactory, scheduleConstraintValidationFacade, validationStartTime, validationEndTime)

        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(nurse("Nurse 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(nurse("Nurse 2")).shift(NIGHT))
                .onDay(1, employeeShiftAssignment().employee(babySitter("Baby sitter 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(babySitter("Baby sitter 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(nurse("Nurse 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(babySitter("Baby sitter 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(babySitter("Baby sitter 2")).shift(NIGHT))
                .numberOfChildren(3)
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
        def solver = new TabuSearchSolver(neighbourhoodStrategyFactory, scheduleConstraintValidationFacade, validationStartTime, validationEndTime)

        def originalSchedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(nurse("Nurse 1")).shift(DAY_OFF))
                .onDay(1, employeeShiftAssignment().employee(nurse("Nurse 2")).shift(NIGHT))
                .onDay(1, employeeShiftAssignment().employee(babySitter("Baby sitter 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(babySitter("Baby sitter 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(nurse("Nurse 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(babySitter("Baby sitter 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(babySitter("Baby sitter 2")).shift(NIGHT))
                .numberOfChildren(3)
                .build()

        when:
        def foundSchedule = solver.findFeasibleSchedule(originalSchedule)

        then:
        def expectedSchedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(nurse("Nurse 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(nurse("Nurse 2")).shift(NIGHT))
                .onDay(1, employeeShiftAssignment().employee(babySitter("Baby sitter 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(babySitter("Baby sitter 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(nurse("Nurse 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(babySitter("Baby sitter 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(babySitter("Baby sitter 2")).shift(NIGHT))
                .numberOfChildren(3)
                .build()

        foundSchedule == expectedSchedule
    }

    def "Should find feasible schedule"() {
        given:
        def inFeasibleNursesShifts = [
                //NURSE 1
                [DAY_OFF, DAY, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY, MORNING,
                 DAY_OFF, DAY, DAY, DAY_OFF, DAY, DAY_OFF, DAY_OFF,
                 MORNING, DAY_OFF, DAY_OFF, DAY, MORNING, DAY_OFF, MORNING,
                 DAY, DAY_OFF, DAY_OFF, DAY_OFF, DAY, MORNING, MORNING,
                 DAY_OFF, DAY],

                //NURSE 2
                [DAY_NIGHT, DAY_OFF, DAY_OFF, DAY, NIGHT, DAY_OFF, DAY,
                 NIGHT, DAY_OFF, DAY_OFF, DAY_OFF, NIGHT, NIGHT, DAY_OFF,
                 DAY, NIGHT, DAY_OFF, DAY, DAY_OFF, NIGHT, NIGHT,
                 DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, NIGHT, DAY_OFF,
                 DAY, DAY_OFF],

                //NURSE 3
                [DAY_OFF, NIGHT, NIGHT, DAY_OFF, DAY, DAY_OFF, MORNING,
                 DAY, DAY_NIGHT, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF, DAY,
                 DAY_OFF, DAY_OFF, NIGHT, DAY_OFF, NIGHT, DAY_OFF, DAY_OFF,
                 DAY_NIGHT, DAY_OFF, NIGHT, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF,
                 DAY, DAY_OFF],

                //NURSE 4
                [NIGHT, DAY_OFF, DAY, NIGHT, DAY_OFF, NIGHT, NIGHT,
                 DAY_OFF, NIGHT, NIGHT, DAY_OFF, DAY, MORNING, DAY_OFF,
                 DAY_OFF, VACATION, VACATION, VACATION, VACATION, DAY_OFF, DAY,
                 DAY_OFF, DAY, DAY, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF,
                 DAY, DAY],

                //NURSE 5
                [DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY, NIGHT, DAY_OFF,
                 DAY, DAY_OFF, DAY_OFF, MORNING, DAY_OFF, DAY_OFF, DAY_OFF,
                 DAY_OFF, DAY_OFF, DAY, DAY_OFF, DAY_OFF, DAY, DAY_OFF,
                 DAY_OFF, NIGHT, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF,
                 DAY_OFF, DAY_OFF],

                //NURSE 6
                [DAY_OFF, DAY, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF, DAY,
                 NIGHT, DAY_OFF, DAY_OFF, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF,
                 NIGHT, DAY_OFF, DAY, MORNING, DAY_OFF, DAY, DAY_OFF,
                 DAY_OFF, DAY_OFF, DAY, DAY_NIGHT, DAY_OFF, DAY_OFF, MORNING,
                 DAY_OFF, NIGHT],

                //NURSE 7
                [DAY_OFF, DAY_OFF, DAY, DAY_OFF, DAY_OFF, DAY, DAY_OFF,
                 DAY_OFF, DAY_OFF, DAY_OFF, NIGHT, DAY_OFF, DAY_OFF, DAY_OFF,
                 DAY_OFF, DAY, DAY_OFF, DAY, DAY_OFF, DAY_OFF, DAY_OFF,
                 DAY_OFF, DAY_OFF, DAY, DAY, NIGHT, DAY_OFF, DAY_OFF,
                 DAY_OFF, DAY_OFF],

                //NURSE 8
                [VACATION, VACATION, VACATION, VACATION, VACATION, VACATION, VACATION,
                 DAY_OFF, MORNING, DAY_OFF, DAY_OFF, DAY, NIGHT, NIGHT,
                 DAY_OFF, DAY, DAY_OFF, DAY, DAY_OFF, DAY_OFF, DAY_OFF,
                 DAY, SICK_LEAVE, SICK_LEAVE, SICK_LEAVE, SICK_LEAVE, DAY_OFF, NIGHT,
                 DAY_OFF, DAY],

                //NURSE 9
                [DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_NIGHT, DAY_OFF,
                 DAY_OFF, DAY, NIGHT, DAY_OFF, DAY_OFF, DAY_NIGHT, DAY_OFF,
                 DAY_OFF, DAY, DAY_OFF, DAY, MORNING, DAY_OFF, DAY,
                 DAY, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY, DAY,
                 NIGHT, DAY_OFF],
        ]

        def infeasibleScheduleToBeFixed = schedule()
                .forMonth(SEPTEMBER)
                .forYear(2018)
                .nursesShifts(inFeasibleNursesShifts)
                .numberOfChildren(6)
                .build()

        def validationStartTime = ScheduleValidationUtils.getStandardValidationStartTime(infeasibleScheduleToBeFixed)
        def validationEndTime = ScheduleValidationUtils.getStandardValidationEndTime(infeasibleScheduleToBeFixed)
        def solver = new TabuSearchSolver(neighbourhoodStrategyFactory, scheduleConstraintValidationFacade, validationStartTime, validationEndTime, NONE)

        when:
        def foundSchedule = solver.findFeasibleSchedule(infeasibleScheduleToBeFixed)

        then:
        println "original schedule: "
        println ScheduleAsciiTablePresenter.buildAsciiTableRepresentationOf(infeasibleScheduleToBeFixed)
        println "found schedule: "
        println ScheduleAsciiTablePresenter.buildAsciiTableRepresentationOf(foundSchedule)

        println "original schedule work-length: "
        println ScheduleAsciiTablePresenter.buildAsciiTableOfEmployeeWorkHours(infeasibleScheduleToBeFixed)
        println "found schedule work-length: "
        println ScheduleAsciiTablePresenter.buildAsciiTableOfEmployeeWorkHours(foundSchedule)

        foundSchedule != null
        foundSchedule != infeasibleScheduleToBeFixed
        scheduleConstraintValidationFacade.validate(foundSchedule, validationStartTime, validationEndTime).isFeasible()
    }


}
