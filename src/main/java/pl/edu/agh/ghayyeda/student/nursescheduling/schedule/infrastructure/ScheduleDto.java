package pl.edu.agh.ghayyeda.student.nursescheduling.schedule.infrastructure;

import org.springframework.data.annotation.Id;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.util.UUID;

public class ScheduleDto {

    @Id
    private final UUID id;
    private final String name;
    private final Schedule schedule;

    ScheduleDto(UUID id, String name, Schedule schedule) {
        this.id = id;
        this.name = name;
        this.schedule = schedule;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Schedule getSchedule() {
        return schedule;
    }
}
