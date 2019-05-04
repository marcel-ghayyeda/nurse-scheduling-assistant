package pl.edu.agh.ghayyeda.student.nursescheduling.constraint;

import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.List;

public class ScheduleConstraintValidationResult {

    private final boolean feasible;
    private final double penalty;
    private final Collection<ConstraintViolationsDescription> constraintViolationsDescriptions;


    private ScheduleConstraintValidationResult(boolean feasible) {
        this(feasible, 0);
    }

    private ScheduleConstraintValidationResult(boolean feasible, double penalty) {
        this.feasible = feasible;
        this.penalty = penalty;
        this.constraintViolationsDescriptions = List.of();
    }

    private ScheduleConstraintValidationResult(boolean feasible, double penalty, Collection<ConstraintViolationsDescription> constraintViolationsDescriptions) {
        this.feasible = feasible;
        this.penalty = penalty;
        this.constraintViolationsDescriptions = constraintViolationsDescriptions;
    }


    public static ScheduleConstraintValidationResult ofPenalty(double penalty, Collection<ConstraintViolationsDescription> constraintViolationsDescriptions) {
        Validate.isTrue(penalty >= 0);
        return new ScheduleConstraintValidationResult(penalty <= 0, penalty, constraintViolationsDescriptions);
    }

    public static ScheduleConstraintValidationResult feasibleConstraintValidationResult(double penalty) {
        return new ScheduleConstraintValidationResult(true, penalty);
    }

    @Deprecated
    public static ScheduleConstraintValidationResult notFeasibleConstraintValidationResult() {
        return new ScheduleConstraintValidationResult(false);
    }

    @Deprecated
    public static ScheduleConstraintValidationResult notFeasibleConstraintValidationResult(double penalty) {
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

    public Collection<ConstraintViolationsDescription> getConstraintViolationsDescriptions() {
        return constraintViolationsDescriptions;
    }
}
