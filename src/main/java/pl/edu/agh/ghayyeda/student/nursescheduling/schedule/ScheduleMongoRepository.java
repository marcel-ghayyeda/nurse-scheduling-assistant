package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ScheduleMongoRepository extends MongoRepository<ScheduleDto, UUID> {
}
