package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoConverters extends MongoCustomConversions {


    public MongoConverters() {
        super(List.of(new YearWriteConverter(), new YearReadConverter()));
    }
}
