package pl.edu.agh.ghayyeda.student.nursescheduling.schedule

import pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee
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
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(R))
                .onDay(1, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 1")).shift(D))
                .onDay(2, employeeShiftAssignment().employee(Employee.nurse("Nurse 2")).shift(N))
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
}
