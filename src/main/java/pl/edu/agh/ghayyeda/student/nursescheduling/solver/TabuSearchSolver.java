package pl.edu.agh.ghayyeda.student.nursescheduling.solver;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.benchmark.TimeLogger;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.penaltyaware.PenaltyAwareScheduleConstraintValidationFacade;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static pl.edu.agh.ghayyeda.student.nursescheduling.util.Predicates.not;

public class TabuSearchSolver implements Solver {

    private static final Logger log = LoggerFactory.getLogger(TabuSearchSolver.class);
    private static final int MAXIMUM_NUMBER_OF_ITERATIONS = 130;

    private final PenaltyAwareScheduleConstraintValidationFacade scheduleConstraintValidationFacade;
    private final LocalDateTime validationStartTime;
    private final LocalDateTime validationEndTime;

    public TabuSearchSolver(PenaltyAwareScheduleConstraintValidationFacade scheduleConstraintValidationFacade, LocalDateTime validationStartTime, LocalDateTime validationEndTime) {
        this.scheduleConstraintValidationFacade = scheduleConstraintValidationFacade;
        this.validationStartTime = validationStartTime;
        this.validationEndTime = validationEndTime;
    }

    @Override
    public Schedule findFeasibleSchedule(Schedule initialSchedule) {
        return TimeLogger.measure("Tabu search", () -> {

            var initialScheduleValidation = scheduleConstraintValidationFacade.validate(initialSchedule, validationStartTime, validationEndTime);
            var bestSchedule = Tuple.of(initialSchedule, initialScheduleValidation);
            var currentSchedule = Tuple.of(initialSchedule, initialScheduleValidation);

            var bestScheduleFoundIterationNumber = 0;

            var tabuList = Collections.newSetFromMap(new LinkedHashMap<>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Object, Boolean> eldest) {
                    return size() > 5;
                }
            });
            for (int currentIteration = 0; currentIteration < MAXIMUM_NUMBER_OF_ITERATIONS; currentIteration++) {
                log.debug("Current iteration: {}", currentIteration);
                long iterationStart = System.nanoTime();
                List<Schedule> neighbourCandidates = currentSchedule._1.getNeighbourhood();
                var bestNeighbourResult = neighbourCandidates.stream()
                        .filter(not(tabuList::contains))
                        .parallel()
                        .map(schedule -> Tuple.of(schedule, scheduleConstraintValidationFacade.validate(schedule, validationStartTime, validationEndTime)))
                        .min(feasibleFirst().thenComparing(tuple -> tuple._2.getPenalty()))
                        .orElseThrow();

                if (feasibleFirst().thenComparing(tuple -> tuple._2.getPenalty()).compare(bestNeighbourResult, bestSchedule) < 0) {
                    bestSchedule = bestNeighbourResult;
                    bestScheduleFoundIterationNumber = currentIteration;
                }

                tabuList.add(currentSchedule._1);
                currentSchedule = bestNeighbourResult;
                long iterationEnd = System.nanoTime();
                log.debug("Tabu search iteration took " + Duration.ofNanos(iterationEnd - iterationStart));
            }

            log.debug("Found best schedule in {} iteration", bestScheduleFoundIterationNumber);

            if (scheduleConstraintValidationFacade.validate(bestSchedule._1, validationStartTime, validationEndTime).isFeasible()) {
                return bestSchedule._1;
            } else {
                throw new NoFeasibleScheduleFoundException();
            }
        });
    }

    private Comparator<Tuple2<Schedule, ScheduleConstraintValidationResult>> feasibleFirst() {
        return (tuple1, tuple2) -> {
            if ((tuple1._2.isFeasible() && tuple2._2.isFeasible()) || (!tuple1._2.isFeasible() && !tuple2._2.isFeasible())) {
                return 0;
            } else if (tuple1._2.isFeasible() && !tuple2._2.isFeasible()) {
                return -1;
            } else if (!tuple1._2.isFeasible() && tuple2._2.isFeasible()) {
                return 1;
            }
            throw new IllegalStateException("Should never happen");
        };
    }
}
