package pl.edu.agh.ghayyeda.student.nursescheduling.schedule

import spock.lang.Specification

import static java.time.Month.NOVEMBER
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.EmployeeShiftAssignmentBuilder.employeeShiftAssignment
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*

class ScheduleAsciiTablePresenterTest extends Specification {

    def presenter = new ScheduleAsciiTablePresenter();

    def "Should correctly build human-friendly representation of a schedule"() {
        given:
        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(MORNING))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .build()


        when:
        def humanFriendlyString = presenter.buildAsciiTableRepresentationOf(schedule)

        then:
        humanFriendlyString ==
                "_______________________\n" +
                "|        | Thu 1| Fri 2|\n" +
                "|======================|\n" +
                "| Nurse 1| R    | D    |\n" +
                "| Nurse 2| D    | N    |\n"
    }

    def "Should correctly print work-length table for all employees"() {
        given:
        def schedule = schedule()
                .forMonth(NOVEMBER)
                .forYear(2018)
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(MORNING))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(DAY))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(NIGHT))
                .build()


        when:
        def humanFriendlyString = presenter.buildAsciiTableOfEmployeeWorkHours(schedule)

        then:
        humanFriendlyString ==
                "______________________\n" +
                "| Employee| Work-hours|\n" +
                "|=====================|\n" +
                "| Nurse 1 | 20        |\n" +
                "| Nurse 2 | 24        |\n"
    }
}
