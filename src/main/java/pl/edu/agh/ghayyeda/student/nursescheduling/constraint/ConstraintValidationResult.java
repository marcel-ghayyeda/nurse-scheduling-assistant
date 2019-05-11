package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.List;

public class ConstraintValidationResult {

    private final boolean feasible;
    private final double penalty;
    private final Collection<ConstraintViolationsDescription> constraintViolationsDescriptions;


    private ConstraintValidationResult(boolean feasible) {
        this(feasible, 0);
    }

    private ConstraintValidationResult(boolean feasible, double penalty) {
        this.feasible = feasible;
        this.penalty = penalty;
        this.constraintViolationsDescriptions = List.of();
    }

    private ConstraintValidationResult(boolean feasible, double penalty, Collection<ConstraintViolationsDescription> constraintViolationsDescriptions) {
        this.feasible = feasible;
        this.penalty = penalty;
        this.constraintViolationsDescriptions = constraintViolationsDescriptions;
    }


    public static ConstraintValidationResult ofPenalty(double penalty, Collection<ConstraintViolationsDescription> constraintViolationsDescriptions) {
        Validate.isTrue(penalty >= 0);
        return new ConstraintValidationResult(penalty <= 0, penalty, constraintViolationsDescriptions);
    }

    public static ConstraintValidationResult feasibleConstraintValidationResult(double penalty) {
        return new ConstraintValidationResult(true, penalty);
    }

    @Deprecated
    public static ConstraintValidationResult notFeasibleConstraintValidationResult() {
        return new ConstraintValidationResult(false);
    }

    @Deprecated
    public static ConstraintValidationResult notFeasibleConstraintValidationResult(double penalty) {
        return new ConstraintValidationResult(false, penalty);
    }

    @Deprecated
    public static ConstraintValidationResult feasibleConstraintValidationResult() {
        return new ConstraintValidationResult(true);
    }

    public boolean isFeasible() {
        return feasible;
    }

    public double getPenalty() {
        return penalty;
    }

    public Collection<ConstraintViolationsDescription> getConstraintViolationsDescriptions() {
        return constraintViolationsDescriptions;
    }
}
