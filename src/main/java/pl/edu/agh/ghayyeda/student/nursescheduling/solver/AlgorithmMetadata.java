package pl.edu.agh.ghayyeda.student.nursescheduling.solver;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class AlgorithmMetadata {

    private final int currentIteration;
    private final int maximumIterations;
    private final Collection<Double> penaltiesHistory;
    private final boolean foundFeasibleSchedule;

    public AlgorithmMetadata(boolean foundFeasibleSchedule, int currentIteration, int maximumIterations, Collection<Double> penaltiesHistory) {
        this.foundFeasibleSchedule = foundFeasibleSchedule;
        this.currentIteration = currentIteration;
        this.maximumIterations = maximumIterations;
        this.penaltiesHistory = penaltiesHistory;
    }

    public boolean isFoundFeasibleSchedule() {
        return foundFeasibleSchedule;
    }

    public int getProgressPercentage() {
        return (int) ((currentIteration / (double) maximumIterations) * 100);
    }

    public int getCurrentIteration() {
        return currentIteration;
    }

    public Optional<List<Double>> getLatestPenalties(int size) {
        if (penaltiesHistory.size() < size) {
            return Optional.empty();
        }
        LinkedList<Double> history = new LinkedList<>(penaltiesHistory);
        Collections.reverse(history);
        return Optional.of(
                history.stream()
                        .limit(size)
                        .collect(toList()));
    }
}
