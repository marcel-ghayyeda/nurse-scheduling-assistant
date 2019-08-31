package pl.edu.agh.ghayyeda.student.nursescheduling.constraint.penaltyaware;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraint;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Component
@VisibleForTesting
public class PenaltyAwareScheduleConstraintFactory implements ScheduleConstraintFactory {

    private final PenaltyAwareMinimumRestTimeAfterShift minimumRestTimeAfterShift = new PenaltyAwareMinimumRestTimeAfterShift();
    private final MinimumOvertime minimumOvertime = new MinimumOvertime();

    @Override
    public Collection<ScheduleConstraint> getHardConstraints(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        var requiredNumberOfBabySitters = PenaltyAwareRequiredNumberOfEmployees.between(validationStartTime, validationEndTime, numberOfChildren);
        return List.of(this.minimumRestTimeAfterShift, requiredNumberOfBabySitters);
    }

    @Override
    public Collection<ScheduleConstraint> getSoftConstraints(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        return List.of(this.minimumOvertime);
    }

}
