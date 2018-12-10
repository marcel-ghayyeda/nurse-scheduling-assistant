package pl.edu.agh.ghayyeda.student.nursescheduling.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class YearMonthUtil {

    public static Stream<LocalDate> allDaysOf(YearMonth yearMonth) {
        return IntStream.rangeClosed(1, yearMonth.lengthOfMonth()).mapToObj(yearMonth::atDay);
    }
}
