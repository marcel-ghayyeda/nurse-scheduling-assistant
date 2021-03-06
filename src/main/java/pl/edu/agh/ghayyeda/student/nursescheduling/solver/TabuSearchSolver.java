package pl.edu.agh.ghayyeda.student.nursescheduling.solver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ghayyeda.student.nursescheduling.benchmark.TimeLogger;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.penaltyaware.PenaltyAwareScheduleConstraintValidationFacade;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.neighbourhood.NeighbourhoodStrategyFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleAsciiTablePresenter.buildAsciiTableRepresentationOf;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.neighbourhood.Adaptation.WIDE;
import static pl.edu.agh.ghayyeda.student.nursescheduling.util.Predicates.not;

public class TabuSearchSolver implements Solver {

    private static final Logger log = LoggerFactory.getLogger(TabuSearchSolver.class);
    private static final int MAXIMUM_NUMBER_OF_ITERATIONS = 800;

    private final PenaltyAwareScheduleConstraintValidationFacade scheduleConstraintValidationFacade;
    private final NeighbourhoodStrategyFactory neighbourhoodStrategyFactory;
    private final LocalDateTime validationStartTime;
    private final LocalDateTime validationEndTime;
    private final SolverAccuracy solverAccuracy;

    public TabuSearchSolver(NeighbourhoodStrategyFactory neighbourhoodStrategyFactory, PenaltyAwareScheduleConstraintValidationFacade scheduleConstraintValidationFacade, LocalDateTime validationStartTime, LocalDateTime validationEndTime) {
        this.scheduleConstraintValidationFacade = scheduleConstraintValidationFacade;
        this.neighbourhoodStrategyFactory = neighbourhoodStrategyFactory;
        this.validationStartTime = validationStartTime;
        this.validationEndTime = validationEndTime;
        this.solverAccuracy = SolverAccuracy.BEST;
    }

    public TabuSearchSolver(NeighbourhoodStrategyFactory neighbourhoodStrategyFactory, PenaltyAwareScheduleConstraintValidationFacade scheduleConstraintValidationFacade, LocalDateTime validationStartTime, LocalDateTime validationEndTime, SolverAccuracy solverAccuracy) {
        this.scheduleConstraintValidationFacade = scheduleConstraintValidationFacade;
        this.neighbourhoodStrategyFactory = neighbourhoodStrategyFactory;
        this.validationStartTime = validationStartTime;
        this.validationEndTime = validationEndTime;
        this.solverAccuracy = solverAccuracy;
    }

    @Override
    public Schedule findFeasibleSchedule(Schedule initialSchedule) {
        return TimeLogger.measure("Tabu search", () -> internalFindFeasibleSchedule(initialSchedule));
    }

    private Schedule internalFindFeasibleSchedule(Schedule initialSchedule) {
        final var initialValidatedSchedule = new ValidatedSchedule(initialSchedule, validate(initialSchedule));
        var bestValidatedSchedule = initialValidatedSchedule;
        var currentValidatedSchedule = initialValidatedSchedule;

        int firstFeasibleScheduleFoundIterationNumber = initialValidatedSchedule.isFeasible() ? 0 : -1;
        int bestScheduleFoundIterationNumber = -1;

        final var tabuSet = TabuSet.newInstance();
        Map<Integer, Double> pentalyByIteration = new LinkedHashMap<>();

        int currentIteration = 0;
        while (shouldProceed(firstFeasibleScheduleFoundIterationNumber, currentIteration)) {
            currentIteration++;
            log.debug("Current iteration: {}", currentIteration);
            final var maybeBestCandidate = findBestNeighbourCandidate(new AlgorithmMetadata(firstFeasibleScheduleFoundIterationNumber > -1, currentIteration, MAXIMUM_NUMBER_OF_ITERATIONS, pentalyByIteration.values()), currentValidatedSchedule, tabuSet);

            if (!maybeBestCandidate.isPresent()) {
                break;
            }
            final var bestValidatedCandidate = maybeBestCandidate.get();

            if (candidateIsBetterThanCurrentBestSchedule(bestValidatedSchedule, bestValidatedCandidate)) {
                bestValidatedSchedule = bestValidatedCandidate;
                bestScheduleFoundIterationNumber = currentIteration;
            }

            if (isFirstFeasibleScheduleFound(firstFeasibleScheduleFoundIterationNumber, bestValidatedCandidate)) {
                firstFeasibleScheduleFoundIterationNumber = currentIteration;
                log.debug("Found first feasible schedule");
            }

            tabuSet.add(currentValidatedSchedule.getSchedule());
            currentValidatedSchedule = bestValidatedCandidate;
            double penalty = currentValidatedSchedule.getConstraintValidationResult().getPenalty();
            log.debug("Penalty: " + penalty);
            pentalyByIteration.put(currentIteration, penalty);
        }

        log.debug("PENALTY BY ITERATION NUMBER");
        log.debug("------");
        pentalyByIteration.forEach((it, penalty) -> System.out.println(it + "," + new BigDecimal(penalty).setScale(20, RoundingMode.HALF_UP)));
        log.debug("------");
        log.debug("Found best schedule in {} iteration", bestScheduleFoundIterationNumber);
        log.debug(buildAsciiTableRepresentationOf(bestValidatedSchedule.getSchedule()));
        if (bestValidatedSchedule.isFeasible()) {
            return bestValidatedSchedule.getSchedule();
        } else {
            throw new NoFeasibleScheduleFoundException();
        }
    }

    private boolean candidateIsBetterThanCurrentBestSchedule(ValidatedSchedule bestValidatedSchedule, ValidatedSchedule bestValidatedCandidates) {
        return bestScheduleFirst().compare(bestValidatedCandidates, bestValidatedSchedule) < 0;
    }

    private boolean isFirstFeasibleScheduleFound(int firstFeasibleScheduleFoundIterationNumber, ValidatedSchedule bestValidatedCandidates) {
        return firstFeasibleScheduleFoundIterationNumber == -1 && bestValidatedCandidates.getConstraintValidationResult().isFeasible();
    }

    private Optional<ValidatedSchedule> findBestNeighbourCandidate(AlgorithmMetadata algorithmMetadata, ValidatedSchedule currentValidatedSchedule, Set<Schedule> tabuSet) {
        if (algorithmMetadata.isFoundFeasibleSchedule()) {
            for (int i = 0; i < 20; i++) {
                Optional<ValidatedSchedule> result = neighbourhoodStrategyFactory.createRandomSimpleNeighbourhoodStrategy()
                        .createNeighbourhood(currentValidatedSchedule.getSchedule(), currentValidatedSchedule.getConstraintValidationResult())
                        .filter(not(tabuSet::contains))
                        .parallel()
                        .map(schedule -> new ValidatedSchedule(schedule, validate(schedule))).collect(toList()).stream()
                        .min(bestScheduleFirst());
                if (result.isPresent()) {
                    return result;
                }
            }
            return Optional.empty();
        }
        var result = neighbourhoodStrategyFactory.createNeighbourhoodStrategy(algorithmMetadata, currentValidatedSchedule.getConstraintValidationResult())
                .createNeighbourhood(currentValidatedSchedule.getSchedule(), currentValidatedSchedule.getConstraintValidationResult())
                .filter(not(tabuSet::contains))
                .parallel()
                .map(schedule -> new ValidatedSchedule(schedule, validate(schedule))).collect(toList()).stream()
                .min(bestScheduleFirst());

        if (!result.isPresent()) {
            result = neighbourhoodStrategyFactory.createAdaptiveNeighbourhoodStrategy(WIDE)
                    .createNeighbourhood(currentValidatedSchedule.getSchedule(), currentValidatedSchedule.getConstraintValidationResult())
                    .filter(not(tabuSet::contains))
                    .parallel()
                    .map(schedule -> new ValidatedSchedule(schedule, validate(schedule))).collect(toList()).stream()
                    .min(bestScheduleFirst());

            if (!result.isPresent()) {
                return neighbourhoodStrategyFactory.createSimpleNeighbourhoodStrategy()
                        .createNeighbourhood(currentValidatedSchedule.getSchedule(), currentValidatedSchedule.getConstraintValidationResult())
                        .filter(not(tabuSet::contains))
                        .parallel()
                        .map(schedule -> new ValidatedSchedule(schedule, validate(schedule))).collect(toList()).stream()
                        .min(bestScheduleFirst());
            }
        }
        return result;
    }

    private Comparator<ValidatedSchedule> bestScheduleFirst() {
        return feasibleFirst().thenComparing(valitedSchedule -> valitedSchedule.getConstraintValidationResult().getPenalty());
    }

    private ConstraintValidationResult validate(Schedule schedule) {
        return scheduleConstraintValidationFacade.validate(schedule, validationStartTime, validationEndTime);
    }

    private boolean shouldProceed(int firstFeasibleScheduleFoundIterationNumber, int currentIteration) {
        return firstFeasibleScheduleFoundIterationNumber == -1 ? currentIteration < MAXIMUM_NUMBER_OF_ITERATIONS : (currentIteration - firstFeasibleScheduleFoundIterationNumber < solverAccuracy.getNumberOfIterationsAfterFeasibleFound());
    }

    private Comparator<ValidatedSchedule> feasibleFirst() {
        return (valitedSchedule1, validatedSchedule2) -> {
            if ((valitedSchedule1.isFeasible() && validatedSchedule2.isFeasible()) || (!valitedSchedule1.isFeasible() && !validatedSchedule2.isFeasible())) {
                return 0;
            } else if (valitedSchedule1.isFeasible() && !validatedSchedule2.isFeasible()) {
                return -1;
            } else if (!valitedSchedule1.isFeasible() && validatedSchedule2.isFeasible()) {
                return 1;
            }
            throw new IllegalStateException("Should never happen");
        };
    }

    private static class ValidatedSchedule {
        private final Schedule schedule;
        private final ConstraintValidationResult constraintValidationResult;

        private ValidatedSchedule(Schedule schedule, ConstraintValidationResult constraintValidationResult) {
            this.schedule = schedule;
            this.constraintValidationResult = constraintValidationResult;
        }

        private Schedule getSchedule() {
            return schedule;
        }

        private ConstraintValidationResult getConstraintValidationResult() {
            return constraintValidationResult;
        }

        private boolean isFeasible() {
            return getConstraintValidationResult().isFeasible();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValidatedSchedule that = (ValidatedSchedule) o;
            return Objects.equals(schedule, that.schedule) &&
                    Objects.equals(constraintValidationResult, that.constraintValidationResult);
        }

        @Override
        public int hashCode() {

            return Objects.hash(schedule, constraintValidationResult);
        }
    }
}
