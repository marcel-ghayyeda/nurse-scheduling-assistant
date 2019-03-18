package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import org.apache.commons.lang3.Validate;

public class ScheduleConstraintValidationResult {

    private final boolean feasible;
    private final double penalty;

    private ScheduleConstraintValidationResult(boolean feasible) {
        this(feasible, 0);
    }

    private ScheduleConstraintValidationResult(boolean feasible, double penalty) {
        this.feasible = feasible;
        this.penalty = penalty;
    }

    public static ScheduleConstraintValidationResult ofPenalty(double penalty) {
        Validate.isTrue(penalty >= 0);
        return new ScheduleConstraintValidationResult(penalty <= 0, penalty);
    }

    @Deprecated
    public static ScheduleConstraintValidationResult notFeasibleConstraintValidationResult() {
        return new ScheduleConstraintValidationResult(false);
    }

    @Deprecated
    static ScheduleConstraintValidationResult notFeasibleConstraintValidationResult(double penalty) {
        return new ScheduleConstraintValidationResult(false, penalty);
    }

    @Deprecated
    public static ScheduleConstraintValidationResult feasibleConstraintValidationResult() {
        return new ScheduleConstraintValidationResult(true);
    }

    public boolean isFeasible() {
        return feasible;
    }

    public double getPenalty() {
        return penalty;
    }
}
