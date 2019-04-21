package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ScheduleDao {

    List<ScheduleDto> getLatestSchedules();

    Optional<ScheduleDto> getById(UUID id);

    UUID save(Schedule schedule);

    UUID save(Schedule schedule, String name);

    UUID save(UUID id, Schedule schedule, String name);
}
