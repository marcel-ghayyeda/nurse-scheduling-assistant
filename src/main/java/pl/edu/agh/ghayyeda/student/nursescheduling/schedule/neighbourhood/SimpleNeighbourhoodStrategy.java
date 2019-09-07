package pl.edu.agh.ghayyeda.student.nursescheduling.schedule.neighbourhood;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

public class SimpleNeighbourhoodStrategy extends AbstractNeighbourhoodStrategy implements NeighbourhoodStrategy {

    private static final Logger log = LoggerFactory.getLogger(SimpleNeighbourhoodStrategy.class);
    private final double probability;

    public SimpleNeighbourhoodStrategy() {
        this.probability = 1.0;
    }

    SimpleNeighbourhoodStrategy(double probability) {
        this.probability = probability;
    }

    @Override
    public Stream<Schedule> createNeighbourhood(Schedule schedule, ConstraintValidationResult ignored) {
        return Stream.of(swapShiftsInTheSameDaysBetweenEmployees(schedule), addWorkingShifts(schedule), removeShifts(schedule))
                .flatMap(identity())
                .distinct();
    }


    private Stream<Schedule> swapShiftsInTheSameDaysBetweenEmployees(Schedule schedule) {
        return schedule.getDateShiftAssignmentMatching(x -> isWorkDayOrDayOff(x) && ThreadLocalRandom.current().nextDouble() < probability)
                .flatMap(shiftAssignment1 ->
                        schedule.getDateShiftAssignmentMatching(isEligibleToSwap(shiftAssignment1))
                                .filter(shiftAssignment2 -> schedule.isAllowedShift(shiftAssignment2.getEmployee(), shiftAssignment1.getShift()))
                                .map(shiftAssignment2 -> createNeighbourWithSwappedShifts(schedule, shiftAssignment1, shiftAssignment2)));
    }


    @VisibleForTesting
    Stream<Schedule> addWorkingShifts(Schedule schedule) {
        return schedule.getDateShiftAssignmentMatching(x -> isWorkDayOrDayOff(x) && ThreadLocalRandom.current().nextDouble() < probability)
                .flatMap(createNeighboursWithAllWorkingShifts(schedule));
    }

    @VisibleForTesting
    Stream<Schedule> removeShifts(Schedule schedule) {
        return schedule.getDateShiftAssignmentMatching(x -> x.isWorkDay() && ThreadLocalRandom.current().nextDouble() < probability)
                .map(createNeighbourWithDayOffShift(schedule));
    }

}
