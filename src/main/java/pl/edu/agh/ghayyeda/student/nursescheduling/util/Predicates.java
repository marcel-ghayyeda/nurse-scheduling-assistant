package pl.edu.agh.ghayyeda.student.nursescheduling.util;

import java.util.function.Predicate;

public abstract class Predicates {

    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }

    public static <T> Predicate<T> of(Predicate<T> predicate) {
        return predicate;
    }
}
