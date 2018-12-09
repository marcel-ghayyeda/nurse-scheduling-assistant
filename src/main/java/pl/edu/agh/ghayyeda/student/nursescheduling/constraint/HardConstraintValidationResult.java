package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import java.util.OptionalInt;

public enum HardConstraintValidationResult implements ScheduleConstraintValidationResult {

    NOT_FEASIBLE(false),
    FEASIBLE(true);

    private final boolean feasible;

    HardConstraintValidationResult(boolean feasible) {
        this.feasible = feasible;
    }

    public static ScheduleConstraintValidationResult notFeasibleConstraintValidationResult() {
        return NOT_FEASIBLE;
    }

    public static ScheduleConstraintValidationResult feasibleConstraintValidationResult() {
        return FEASIBLE;
    }

    @Override
    public boolean isFeasible() {
        return feasible;
    }

    @Override
    public OptionalInt getPenalty() {
        return OptionalInt.empty();
    }
}
