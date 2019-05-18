package pl.edu.agh.ghayyeda.student.nursescheduling.solver;

public class AlgorithmMetadata {

    private final int currentIteration;
    private final int maximumIterations;

    public AlgorithmMetadata(int currentIteration, int maximumIterations) {
        this.currentIteration = currentIteration;
        this.maximumIterations = maximumIterations;
    }

    public int getProgressPercentage() {
        return (currentIteration / maximumIterations) * 100;
    }

}
