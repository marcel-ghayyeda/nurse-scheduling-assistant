package pl.edu.agh.ghayyeda.student.nursescheduling.common;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@ReadingConverter
public class YearReadConverter implements Converter<Integer, Year> {
    @Override
    public Year convert(Integer year) {
        return Year.of(year);
    }
}
