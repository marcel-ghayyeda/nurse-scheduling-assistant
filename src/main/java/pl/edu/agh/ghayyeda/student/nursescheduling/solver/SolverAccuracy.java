package pl.edu.agh.ghayyeda.student.nursescheduling.solver;

public enum SolverAccuracy {
    LOW(20),
    MEDIUM(40),
    HIGH(60),
    BEST(80);

    private final int numberOfIterationsAfterFeasibleFound;

    SolverAccuracy(int numberOfIterationsAfterFeasibleFound) {
        this.numberOfIterationsAfterFeasibleFound = numberOfIterationsAfterFeasibleFound;
    }

    public int getNumberOfIterationsAfterFeasibleFound() {
        return numberOfIterationsAfterFeasibleFound;
    }
}
