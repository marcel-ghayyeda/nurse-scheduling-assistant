package pl.edu.agh.ghayyeda.student.nursescheduling.schedule.infrastructure;

import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleDao {

    List<ScheduleDto> getAll();

    Optional<ScheduleDto> getById(UUID id);

    UUID save(Schedule schedule);

    UUID save(Schedule schedule, String name);

    UUID save(UUID id, Schedule schedule, String name);

    void delete(UUID scheduleId);
}
