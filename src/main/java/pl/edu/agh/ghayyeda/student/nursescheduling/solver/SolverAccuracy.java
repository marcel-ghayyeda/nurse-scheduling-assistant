package pl.edu.agh.ghayyeda.student.nursescheduling.solver;

public enum SolverAccuracy {
    LOW(30),
    MEDIUM(80),
    HIGH(130),
    BEST(200);

    private final int maximumNumberOfInterations;

    SolverAccuracy(int maximumNumberOfInterations) {
        this.maximumNumberOfInterations = maximumNumberOfInterations;
    }

    public int getMaximumNumberOfInterations() {
        return maximumNumberOfInterations;
    }
}
