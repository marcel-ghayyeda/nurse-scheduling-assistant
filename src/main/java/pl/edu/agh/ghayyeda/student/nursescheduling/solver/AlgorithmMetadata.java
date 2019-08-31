package pl.edu.agh.ghayyeda.student.nursescheduling.solver;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class AlgorithmMetadata {

    private final int currentIteration;
    private final int maximumIterations;
    private final Collection<Double> penaltiesHistory;

    public AlgorithmMetadata(int currentIteration, int maximumIterations, Collection<Double> penaltiesHistory) {
        this.currentIteration = currentIteration;
        this.maximumIterations = maximumIterations;
        this.penaltiesHistory = penaltiesHistory;
    }

    public int getProgressPercentage() {
        return (int) ((currentIteration / (double) maximumIterations) * 100);
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
