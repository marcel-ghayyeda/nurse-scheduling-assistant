package pl.edu.agh.ghayyeda.student.nursescheduling.solver;

import org.slf4j.Logger;
import pl.edu.agh.ghayyeda.student.nursescheduling.benchmark.TimeLogger;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationFacade;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import static org.slf4j.LoggerFactory.getLogger;

public class BfsSolver implements Solver {

    private static final Logger log = getLogger(BfsSolver.class);

    private final ScheduleConstraintValidationFacade scheduleConstraintValidationFacade;
    private LocalDateTime validationStartTime;
    private LocalDateTime validationEndTime;

    public BfsSolver(ScheduleConstraintValidationFacade scheduleConstraintValidationFacade, LocalDateTime validationStartTime, LocalDateTime validationEndTime) {
        this.scheduleConstraintValidationFacade = scheduleConstraintValidationFacade;
        this.validationStartTime = validationStartTime;
        this.validationEndTime = validationEndTime;
    }

    @Override
    public Schedule findFeasibleSchedule(Schedule schedule) {
        return TimeLogger.measure("BfsSolver", () -> {
            Queue<Schedule> queue = new LinkedList<>();
            HashSet<Schedule> history = new HashSet<>();
            if (isFeasible(schedule)) {
                return schedule;
            } else {
                queue.add(schedule);
                return bfs(queue, history).orElseThrow();
            }
        });
    }

    private Optional<Schedule> bfs(Queue<Schedule> queue, HashSet<Schedule> history) {
        while (!queue.isEmpty()) {
            log.debug("while starts");
            Schedule schedule = queue.remove();
            for (Schedule neighbour : schedule.getNeighbourhood()) {
                if (history.contains(neighbour)) {
                    log.debug("Rejecting neighbour due to history");
                    continue;
                } else {
                    history.add(neighbour);
                }
                log.debug("for");
                if (isFeasible(neighbour)) {
                    return Optional.of(neighbour);
                } else {
                    queue.add(neighbour);
                }
            }
        }
        return Optional.empty();
    }

    private boolean isFeasible(Schedule schedule) {
        return scheduleConstraintValidationFacade.validate(schedule, validationStartTime, validationEndTime).isFeasible();
    }
}
