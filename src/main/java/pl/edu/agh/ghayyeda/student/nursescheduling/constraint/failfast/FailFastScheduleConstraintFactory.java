package pl.edu.agh.ghayyeda.student.nursescheduling.constraint.failfast;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraint;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Component
@VisibleForTesting
public class FailFastScheduleConstraintFactory implements ScheduleConstraintFactory {

    @Override
    public Collection<ScheduleConstraint> getHardConstraints(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        var minimumRestTimeAfterShift = new FailFastMinimumRestTimeAfterShift();
        var requiredNumberOfBabySitters = FailFastRequiredNumberOfEmployees.between(validationStartTime, validationEndTime, numberOfChildren);
        return List.of(minimumRestTimeAfterShift, requiredNumberOfBabySitters);
    }

    @Override
    public Collection<ScheduleConstraint> getSoftConstraints(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        return List.of();
    }

}
