package pl.edu.agh.ghayyeda.student.nursescheduling.schedule

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee
import spock.lang.Specification

import static java.time.Month.NOVEMBER
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignmentBuilder.employeeShiftAssignment
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*

class ScheduleTest extends Specification {

    def "Should correctly compare schedules using equals and hashcode"() {
        given:
        def schedule1 = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(MORNING))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .build()

        def schedule1Copy = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(MORNING))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .build()

        def anotherSchedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(MORNING))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY))
                .build()

        expect:
        schedule1.hashCode() == schedule1Copy.hashCode()
        schedule1.equals(schedule1Copy)

        schedule1.hashCode() != anotherSchedule.hashCode()
        !schedule1.equals(anotherSchedule)
    }

    def "Should create new schedules by adding exactly one working shift in place of free shift"() {
        given:
        def originalSchedule = baseScheduleBuilderWithAllWorkingShifts()
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
                .build()


        when:
        def newSchedules = originalSchedule.addRandomShifts().collect()

        then:
        newSchedules.size() == 5
        newSchedules.contains(baseScheduleBuilderWithAllWorkingShifts()
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(MORNING))
                .build())
        newSchedules.contains(baseScheduleBuilderWithAllWorkingShifts()
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(AFTERNOON))
                .build())
        newSchedules.contains(baseScheduleBuilderWithAllWorkingShifts()
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .build())
        newSchedules.contains(baseScheduleBuilderWithAllWorkingShifts()
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(NIGHT))
                .build())
        newSchedules.contains(baseScheduleBuilderWithAllWorkingShifts()
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_NIGHT))
                .build())

    }

    private static ScheduleBuilder baseScheduleBuilderWithAllWorkingShifts() {
        schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
    }

    def "Should create new scheduled by removing exactly one working shift and replacing it with free shift"() {
        given:
        def originalSchedule = baseScheduleBuilderWithTwoFreeShifts()
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .build()


        when:
        def newSchedules = originalSchedule.removeRandomShifts().collect()

        then:
        newSchedules.size() == 4
        newSchedules.contains(baseScheduleBuilderWithTwoFreeShifts()
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY_OFF))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .build())
        newSchedules.contains(baseScheduleBuilderWithTwoFreeShifts()
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .build())
        newSchedules.contains(baseScheduleBuilderWithTwoFreeShifts()
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY_OFF))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .build())
        newSchedules.contains(baseScheduleBuilderWithTwoFreeShifts()
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY_OFF))
                .build())
    }

    private static ScheduleBuilder baseScheduleBuilderWithTwoFreeShifts() {
        schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY_OFF))
    }
}
