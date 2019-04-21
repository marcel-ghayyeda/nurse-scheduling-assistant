package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.springframework.stereotype.Service;

import java.util.*;

import static java.time.Month.SEPTEMBER;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Shift.*;

@Service
class MockScheduleDao implements ScheduleDao {

    private final Map
            <UUID, ScheduleDto> scheduleRepository;

    MockScheduleDao() {
        var feasibleSchedule = schedule()
                .forMonth(SEPTEMBER)
                .forYear(2018)
                .nursesShifts(feasibleShifts)
                .numberOfChildren(3)
                .build();

        var notFeasibleSchedule = schedule()
                .forMonth(SEPTEMBER)
                .forYear(2018)
                .nursesShifts(notFeasibleShifts)
                .numberOfChildren(3)
                .build();

        var notFeasibleSolvableSchedule = schedule()
                .forMonth(SEPTEMBER)
                .forYear(2018)
                .nursesShifts(notFeasibleSolvableShifts)
                .numberOfChildren(6)
                .build();


        UUID feasibleScheduleId = UUID.fromString("ce88d31a-5a41-4682-a333-1b4e141498e7");
        UUID notFeasibleScheduleId = UUID.fromString("c93dec10-5dbc-48ec-89b0-ea551ed333ab");
        UUID notFeasibleSolvableScheduleId = UUID.fromString("060566ef-098b-42ad-89d3-90bb589b5d3a");
        var feasibleScheduleWrapper = new ScheduleDto(feasibleScheduleId, "Test schedule 1", feasibleSchedule);
        var notFeasibleScheduleWrapper = new ScheduleDto(notFeasibleScheduleId, "Test schedule 2", notFeasibleSchedule);
        var notFeasibleSolvableScheduleWrapper = new ScheduleDto(notFeasibleSolvableScheduleId, "Solvable schedule", notFeasibleSolvableSchedule);

        Map<UUID, ScheduleDto> scheduleMap = new LinkedHashMap<>();
        scheduleMap.put(feasibleScheduleId, feasibleScheduleWrapper);
        scheduleMap.put(notFeasibleScheduleId, notFeasibleScheduleWrapper);
        scheduleMap.put(notFeasibleSolvableScheduleId, notFeasibleSolvableScheduleWrapper);
        this.scheduleRepository = scheduleMap;
    }

    @Override
    public List<ScheduleDto> getLatestSchedules() {
        return new ArrayList<>(scheduleRepository.values());
    }

    @Override
    public Optional<ScheduleDto> getById(UUID id) {
        return Optional.ofNullable(scheduleRepository.get(id));
    }

    @Override
    public UUID save(Schedule schedule) {
        UUID id = UUID.randomUUID();
        scheduleRepository.put(id, new ScheduleDto(id, "schedule", schedule));
        return id;
    }

    @Override
    public UUID save(Schedule schedule, String name) {
        UUID id = UUID.randomUUID();
        scheduleRepository.put(id, new ScheduleDto(id, name, schedule));
        return id;
    }


    @Override
    public UUID save(UUID id, Schedule schedule, String name) {
        scheduleRepository.put(id, new ScheduleDto(id, name, schedule));
        return id;
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

    private static final List<List<Shift>> notFeasibleSolvableShifts =
            List.of(
                    //NURSE 1
                    List.of(DAY_OFF, DAY, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY, MORNING,
                            DAY_OFF, DAY, DAY, DAY_OFF, DAY, DAY_OFF, DAY_OFF,
                            MORNING, DAY_OFF, DAY_OFF, DAY, MORNING, DAY_OFF, MORNING,
                            DAY, DAY_OFF, DAY_OFF, DAY_OFF, DAY, MORNING, MORNING,
                            DAY_OFF, DAY),

                    //NURSE 2
                    List.of(DAY_NIGHT, DAY_OFF, DAY_OFF, DAY, NIGHT, DAY_OFF, DAY,
                            NIGHT, DAY_OFF, DAY_OFF, DAY_OFF, NIGHT, NIGHT, DAY_OFF,
                            DAY, NIGHT, DAY_OFF, DAY, DAY_OFF, NIGHT, NIGHT,
                            DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, NIGHT, DAY_OFF,
                            DAY, DAY_OFF),

                    //NURSE 3
                    List.of(DAY_OFF, NIGHT, NIGHT, DAY_OFF, DAY, DAY_OFF, MORNING,
                            DAY, DAY_NIGHT, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF, DAY,
                            DAY_OFF, DAY_OFF, NIGHT, DAY_OFF, NIGHT, DAY_OFF, DAY_OFF,
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
                            DAY, DAY_OFF, DAY_OFF, MORNING, DAY_OFF, DAY_OFF, DAY_OFF,
                            DAY_OFF, DAY_OFF, DAY, DAY_OFF, DAY_OFF, DAY, DAY_OFF,
                            DAY_OFF, NIGHT, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF, DAY_OFF,
                            DAY_OFF, DAY_OFF),

                    //NURSE 6
                    List.of(DAY_OFF, DAY, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF, DAY,
                            NIGHT, DAY_OFF, DAY_OFF, DAY_OFF, DAY_NIGHT, DAY_OFF, DAY_OFF,
                            NIGHT, DAY_OFF, DAY, MORNING, DAY_OFF, DAY, DAY_OFF,
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
                            DAY_OFF, DAY, NIGHT, DAY_OFF, DAY_OFF, DAY_NIGHT, DAY_OFF,
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
                    DAY_OFF, DAY, DAY_OFF, DAY, DAY, DAY_OFF, DAY_OFF,
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
