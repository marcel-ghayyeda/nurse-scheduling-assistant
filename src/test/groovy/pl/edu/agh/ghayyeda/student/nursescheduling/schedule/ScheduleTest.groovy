package pl.edu.agh.ghayyeda.student.nursescheduling.schedule

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee
import spock.lang.Specification

import static java.time.Month.NOVEMBER
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignmentBuilder.employeeShiftAssignment
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*

class ScheduleTest extends Specification {

    def "Should create new Schedule by adding exactly one working shift in place of free shift"() {
        given:
        def originalSchedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(W))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(W))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .build()


        when:
        def newSchedule = originalSchedule.addRandomShift()

        then:
        def numberOfDateEmployeeShiftAssignmentDifferences = 0
        def numberOfAddedWorkShifts = 0
        for (int i = 0; i < originalSchedule.dateEmployeeShiftAssignmentsByDate.size(); i++) {
            if (originalSchedule.dateEmployeeShiftAssignmentsByDate.get(i) != newSchedule.dateEmployeeShiftAssignmentsByDate.get(i)) {
                def originalShiftAssignments = originalSchedule.dateEmployeeShiftAssignmentsByDate.get(i).shiftAssignments
                def newShiftAssignments = newSchedule.dateEmployeeShiftAssignmentsByDate.get(i).shiftAssignments
                for (int j = 0; j < originalShiftAssignments.size(); j++) {
                    if (originalShiftAssignments.get(j) != newShiftAssignments.get(j)) {
                        numberOfDateEmployeeShiftAssignmentDifferences++
                        if (newShiftAssignments.get(j).isWorkDay() && !originalShiftAssignments.get(j).isWorkDay())
                            numberOfAddedWorkShifts++
                    }
                }
            }
        }

        numberOfDateEmployeeShiftAssignmentDifferences == 1
        numberOfAddedWorkShifts == 1

    }

    def "Should create new Schedule by removing exactly one working shift and replacing it with free shift"() {
        given:
        def originalSchedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(W))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(W))
                .onDay(3, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
                .build()


        when:
        def newSchedule = originalSchedule.removeRandomShift()

        then:
        def numberOfDateEmployeeShiftAssignmentDifferences = 0
        def numberOfAddedFreeShifts = 0
        for (int i = 0; i < originalSchedule.dateEmployeeShiftAssignmentsByDate.size(); i++) {
            if (originalSchedule.dateEmployeeShiftAssignmentsByDate.get(i) != newSchedule.dateEmployeeShiftAssignmentsByDate.get(i)) {
                def originalShiftAssignments = originalSchedule.dateEmployeeShiftAssignmentsByDate.get(i).shiftAssignments
                def newShiftAssignments = newSchedule.dateEmployeeShiftAssignmentsByDate.get(i).shiftAssignments
                for (int j = 0; j < originalShiftAssignments.size(); j++) {
                    if (originalShiftAssignments.get(j) != newShiftAssignments.get(j)) {
                        numberOfDateEmployeeShiftAssignmentDifferences++
                        if (!newShiftAssignments.get(j).isWorkDay() && originalShiftAssignments.get(j).isWorkDay())
                            numberOfAddedFreeShifts++
                    }
                }
            }
        }

        numberOfDateEmployeeShiftAssignmentDifferences == 1
        numberOfAddedFreeShifts == 1

    }
}
