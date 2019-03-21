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

    @Override
    public Collection<ScheduleConstraint> get(LocalDateTime validationStartTime, LocalDateTime validationEndTime, int numberOfChildren) {
        var minimumRestTimeAfterShift = new PenaltyAwareMinimumRestTimeAfterShift();
        var requiredNumberOfBabySitters = PenaltyAwareRequiredNumberOfEmployees.between(validationStartTime, validationEndTime, numberOfChildren);
        var minimumOvertime = new MinimumOvertime();
        return List.of(minimumRestTimeAfterShift, requiredNumberOfBabySitters, minimumOvertime);
    }

}
