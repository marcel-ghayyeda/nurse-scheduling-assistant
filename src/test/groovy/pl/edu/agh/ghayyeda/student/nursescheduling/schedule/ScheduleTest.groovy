package pl.edu.agh.ghayyeda.student.nursescheduling.schedule

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee
import spock.lang.Specification

import static java.time.Month.NOVEMBER
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignmentBuilder.employeeShiftAssignment
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*

class ScheduleTest extends Specification {

    def "Should create new schedules by adding exactly one working shift in place of free shift"() {
        given:
        def originalSchedule = baseScheduleBuilderWithAllWorkingShifts()
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(W))
                .build()


        when:
        def newSchedules = originalSchedule.addRandomShifts().collect()

        then:
        newSchedules.size() == 5
        newSchedules.contains(baseScheduleBuilderWithAllWorkingShifts()
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(R))
                .build())
        newSchedules.contains(baseScheduleBuilderWithAllWorkingShifts()
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(P))
                .build())
        newSchedules.contains(baseScheduleBuilderWithAllWorkingShifts()
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .build())
        newSchedules.contains(baseScheduleBuilderWithAllWorkingShifts()
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(N))
                .build())
        newSchedules.contains(baseScheduleBuilderWithAllWorkingShifts()
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DN))
                .build())

    }

    private static ScheduleBuilder baseScheduleBuilderWithAllWorkingShifts() {
        schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
    }

    def "Should create new scheduled by removing exactly one working shift and replacing it with free shift"() {
        given:
        def originalSchedule = baseScheduleBuilderWithTwoFreeShifts()
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .build()


        when:
        def newSchedules = originalSchedule.removeRandomShifts().collect()

        then:
        newSchedules.size() == 4
        newSchedules.contains(baseScheduleBuilderWithTwoFreeShifts()
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(W))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .build())
        newSchedules.contains(baseScheduleBuilderWithTwoFreeShifts()
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(W))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .build())
        newSchedules.contains(baseScheduleBuilderWithTwoFreeShifts()
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(W))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .build())
        newSchedules.contains(baseScheduleBuilderWithTwoFreeShifts()
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(W))
                .build())
    }

    private static ScheduleBuilder baseScheduleBuilderWithTwoFreeShifts() {
        schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(W))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(W))
    }
}
