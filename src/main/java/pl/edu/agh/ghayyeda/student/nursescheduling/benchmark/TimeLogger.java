package pl.edu.agh.ghayyeda.student.nursescheduling.benchmark;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;

public class TimeLogger {
    private static final Logger log = getLogger(TimeLogger.class);

    public static <T> T measure(String description, Supplier<T> function) {
        long start = System.nanoTime();
        T result = function.get();
        long end = System.nanoTime();
        var duration = Duration.ofNanos(end - start);
        log.debug(description + " :Invocation took " + duration);
        return result;
    }

}
