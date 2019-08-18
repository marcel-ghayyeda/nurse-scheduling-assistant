package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.springframework.data.annotation.Id;

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

    UUID getId() {
        return id;
    }

    String getName() {
        return name;
    }

    public Schedule getSchedule() {
        return schedule;
    }
}
