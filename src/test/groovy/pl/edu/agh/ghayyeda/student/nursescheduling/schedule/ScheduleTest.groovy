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

        !schedule1.equals(anotherSchedule)
    }

}
