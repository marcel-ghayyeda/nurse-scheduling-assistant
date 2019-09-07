package pl.edu.agh.ghayyeda.student.nursescheduling.schedule.infrastructure;

import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.common.YearReadConverter;
import pl.edu.agh.ghayyeda.student.nursescheduling.common.YearWriteConverter;

import java.util.List;

@Component
public class MongoConverters extends MongoCustomConversions {


    public MongoConverters() {
        super(List.of(new YearWriteConverter(), new YearReadConverter()));
    }
}
