package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@WritingConverter
public class YearWriteConverter implements Converter<Year, Integer> {
    @Override
    public Integer convert(Year year) {
        return year.getValue();
    }
}
