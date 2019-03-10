package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.springframework.stereotype.Service;

import java.util.*;

import static java.time.Month.SEPTEMBER;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*;

@Service
class MockScheduleDao implements ScheduleDao {


    private final Map<UUID, ScheduleDto> scheduleRepository;

    MockScheduleDao() {
        var feasibleSchedule = schedule()
                .forMonth(SEPTEMBER)
                .forYear(2018)
                .nursesShifts(feasibleShifts)
                .build();

        var notFeasibleSchedule = schedule()
                .forMonth(SEPTEMBER)
                .forYear(2018)
                .nursesShifts(notFeasibleShifts)
                .build();

        UUID feasibleScheduleId = UUID.fromString("ce88d31a-5a41-4682-a333-1b4e141498e7");
        UUID notFeasibleScheduleId = UUID.fromString("c93dec10-5dbc-48ec-89b0-ea551ed333ab");
        var feasibleScheduleWrapper = new ScheduleDto(feasibleScheduleId, "Test schedule 1", feasibleSchedule);
        var notFeasibleScheduleWrapper = new ScheduleDto(notFeasibleScheduleId, "Test schedule 2", notFeasibleSchedule);

        this.scheduleRepository = Map.of(
                feasibleScheduleId, feasibleScheduleWrapper,
                notFeasibleScheduleId, notFeasibleScheduleWrapper);
    }

    @Override
    public List<ScheduleDto> getLatestSchedules() {
        return new ArrayList<>(scheduleRepository.values());
    }

    @Override
    public Optional<ScheduleDto> getById(UUID id) {
        return Optional.ofNullable(scheduleRepository.get(id));
    }

    private static final List<List<Shift>> notFeasibleShifts = List.of(
            //NURSE 1
            List.of(DAY_OFF, DAY, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY, MORNING,
                    DAY_OFF, DAY, DAY, DAY_OFF, DAY, DAY_OFF, MORNING,
                    MORNING, DAY_OFF, DAY_OFF, DAY, MORNING, DAY_OFF, MORNING,
                    DAY, DAY_OFF, DAY_OFF, DAY_OFF, DAY, MORNING, MORNING,
                    DAY_OFF, DAY),

            //NURSE 2
            List.of(DAY_NIGHT, NIGHT, DAY_OFF, DAY, NIGHT, DAY_OFF, DAY,
                    NIGHT, DAY_OFF, DAY_OFF, DAY_OFF, NIGHT, NIGHT, DAY_OFF,
                    DAY, NIGHT, DAY_OFF, DAY, DAY_OFF, NIGHT, NIGHT,
                    DAY_OFF, DAY_OFF, NIGHT, DAY_OFF, DAY_OFF, NIGHT, DAY_OFF,
                    DAY, DAY_OFF),

            //NURSE 3
            List.of(DAY_OFF, NIGHT, NIGHT, DAY_OFF, DAY, DAY_OFF, MORNING,
                    DAY, DAY_NIGHT, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF, DAY,
                    DAY_NIGHT, DAY_OFF, NIGHT, DAY_OFF, NIGHT, DAY_OFF, DAY_OFF,
                    DAY_NIGHT, NIGHT, NIGHT, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF,
                    DAY, DAY_OFF),

            //NURSE 4
            List.of(NIGHT, DAY_OFF, DAY, NIGHT, DAY_OFF, NIGHT, NIGHT,
                    DAY_OFF, NIGHT, NIGHT, DAY_OFF, DAY, MORNING, DAY_OFF,
                    DAY_OFF, VACATION, VACATION, VACATION, VACATION, DAY_OFF, DAY,
                    DAY_OFF, DAY, DAY, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF,
                    DAY, DAY),

            //NURSE 5
            List.of(DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY, NIGHT, DAY_OFF,
                    DAY, DAY_OFF, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF, DAY_OFF,
                    DAY_OFF, DAY_OFF, DAY, NIGHT, DAY_OFF, DAY, DAY_OFF,
                    DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF,
                    DAY_OFF, DAY_OFF),

            //NURSE 6
            List.of(DAY_OFF, DAY, DAY_OFF, DAY_NIGHT, NIGHT, DAY_OFF, DAY,
                    NIGHT, DAY_OFF, DAY_OFF, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF,
                    NIGHT, DAY_OFF, DAY, NIGHT, DAY_OFF, DAY, DAY_OFF,
                    DAY_OFF, DAY_OFF, DAY, DAY_NIGHT, DAY_OFF, DAY_OFF, MORNING,
                    DAY_OFF, NIGHT),

            //NURSE 7
            List.of(DAY_OFF, DAY_OFF, DAY, DAY_OFF, DAY_OFF, DAY, DAY_OFF,
                    DAY_OFF, DAY_OFF, DAY_OFF, NIGHT, DAY_OFF, DAY_OFF, DAY_OFF,
                    DAY_OFF, DAY, DAY_OFF, DAY, DAY_OFF, DAY_OFF, DAY_OFF,
                    DAY_OFF, DAY_OFF, DAY, DAY, NIGHT, DAY_OFF, DAY_OFF,
                    DAY_OFF, DAY_OFF),

            //NURSE 8
            List.of(VACATION, VACATION, VACATION, VACATION, VACATION, VACATION, VACATION,
                    DAY_OFF, MORNING, DAY_OFF, DAY_OFF, DAY, NIGHT, DAY_OFF,
                    DAY_OFF, DAY, DAY_OFF, DAY, DAY_OFF, DAY_OFF, DAY_OFF,
                    DAY, SICK_LEAVE, SICK_LEAVE, SICK_LEAVE, SICK_LEAVE, DAY_OFF, NIGHT,
                    DAY_OFF, DAY),

            //NURSE 9
            List.of(DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_NIGHT, NIGHT,
                    DAY_OFF, DAY, DAY_NIGHT, DAY_OFF, DAY_OFF, DAY_NIGHT, NIGHT,
                    DAY_OFF, DAY, DAY_OFF, DAY, MORNING, DAY_OFF, DAY,
                    DAY, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY, DAY,
                    NIGHT, DAY_OFF)
    );

    private static final List<List<Shift>> feasibleShifts = List.of(
            //NURSE 1
            List.of(DAY_OFF, DAY, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY, MORNING,
                    DAY_OFF, DAY, DAY, DAY_OFF, DAY, DAY_OFF, MORNING,
                    MORNING, DAY_OFF, DAY_OFF, DAY, MORNING, DAY_OFF, MORNING,
                    DAY, DAY_OFF, DAY_OFF, DAY_OFF, DAY, MORNING, MORNING,
                    DAY_OFF, DAY),

            //NURSE 2
            List.of(DAY_NIGHT, DAY_OFF, DAY_OFF, DAY, NIGHT, DAY_OFF, DAY,
                    NIGHT, DAY_OFF, DAY_OFF, DAY_OFF, NIGHT, NIGHT, DAY_OFF,
                    DAY, NIGHT, DAY_OFF, DAY, DAY_OFF, NIGHT, NIGHT,
                    DAY_OFF, DAY_OFF, NIGHT, DAY_OFF, DAY_OFF, NIGHT, DAY_OFF,
                    DAY, DAY_OFF),

            //NURSE 3
            List.of(DAY_OFF, NIGHT, NIGHT, DAY_OFF, DAY, DAY_OFF, MORNING,
                    DAY, DAY_NIGHT, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF, DAY,
                    DAY_NIGHT, DAY_OFF, NIGHT, DAY_OFF, NIGHT, DAY_OFF, DAY_OFF,
                    DAY_NIGHT, DAY_OFF, NIGHT, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF,
                    DAY, DAY_OFF),

            //NURSE 4
            List.of(NIGHT, DAY_OFF, DAY, NIGHT, DAY_OFF, NIGHT, NIGHT,
                    DAY_OFF, NIGHT, NIGHT, DAY_OFF, DAY, MORNING, DAY_OFF,
                    DAY_OFF, VACATION, VACATION, VACATION, VACATION, DAY_OFF, DAY,
                    DAY_OFF, DAY, DAY, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF,
                    DAY, DAY),

            //NURSE 5
            List.of(DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY, NIGHT, DAY_OFF,
                    DAY, DAY_OFF, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF, DAY_OFF,
                    DAY_OFF, DAY_OFF, DAY, NIGHT, DAY_OFF, DAY, DAY_OFF,
                    DAY_OFF, NIGHT, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF,
                    DAY_OFF, DAY_OFF),

            //NURSE 6
            List.of(DAY_OFF, DAY, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF, DAY,
                    NIGHT, DAY_OFF, DAY_OFF, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF,
                    NIGHT, DAY_OFF, DAY, NIGHT, DAY_OFF, DAY, DAY_OFF,
                    DAY_OFF, DAY_OFF, DAY, DAY_NIGHT, DAY_OFF, DAY_OFF, MORNING,
                    DAY_OFF, NIGHT),

            //NURSE 7
            List.of(DAY_OFF, DAY_OFF, DAY, DAY_OFF, DAY_OFF, DAY, DAY_OFF,
                    DAY_OFF, DAY_OFF, DAY_OFF, NIGHT, DAY_OFF, DAY_OFF, DAY_OFF,
                    DAY_OFF, DAY, DAY_OFF, DAY, DAY_OFF, DAY_OFF, DAY_OFF,
                    DAY_OFF, DAY_OFF, DAY, DAY, NIGHT, DAY_OFF, DAY_OFF,
                    DAY_OFF, DAY_OFF),

            //NURSE 8
            List.of(VACATION, VACATION, VACATION, VACATION, VACATION, VACATION, VACATION,
                    DAY_OFF, MORNING, DAY_OFF, DAY_OFF, DAY, NIGHT, NIGHT,
                    DAY_OFF, DAY, DAY_OFF, DAY, DAY_OFF, DAY_OFF, DAY_OFF,
                    DAY, SICK_LEAVE, SICK_LEAVE, SICK_LEAVE, SICK_LEAVE, DAY_OFF, NIGHT,
                    DAY_OFF, DAY),

            //NURSE 9
            List.of(DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_NIGHT, DAY_OFF,
                    DAY_OFF, DAY, DAY_NIGHT, DAY_OFF, DAY_OFF, DAY_NIGHT, DAY_OFF,
                    DAY_OFF, DAY, DAY_OFF, DAY, MORNING, DAY_OFF, DAY,
                    DAY, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY, DAY,
                    NIGHT, DAY_OFF)
    );


}
