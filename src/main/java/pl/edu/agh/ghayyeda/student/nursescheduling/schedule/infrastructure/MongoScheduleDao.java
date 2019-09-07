package pl.edu.agh.ghayyeda.student.nursescheduling.schedule.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Primary
public class MongoScheduleDao implements ScheduleDao {


    @Autowired
    private ScheduleMongoRepository scheduleMongoRepository;

    @Override
    public List<ScheduleDto> getAll() {
        return scheduleMongoRepository.findAll();
    }

    @Override
    public Optional<ScheduleDto> getById(UUID id) {
        return scheduleMongoRepository.findById(id);
    }

    @Override
    public UUID save(Schedule schedule) {
        UUID id = UUID.randomUUID();
        scheduleMongoRepository.save(new ScheduleDto(id, "new schedule", schedule));
        return id;
    }

    @Override
    public UUID save(Schedule schedule, String name) {
        UUID id = UUID.randomUUID();
        scheduleMongoRepository.save(new ScheduleDto(id, name, schedule));
        return id;
    }

    @Override
    public UUID save(UUID id, Schedule schedule, String name) {
        scheduleMongoRepository.save(new ScheduleDto(id, name, schedule));
        return id;
    }

    @Override
    public void delete(UUID scheduleId) {
        scheduleMongoRepository.deleteById(scheduleId);
    }
}
