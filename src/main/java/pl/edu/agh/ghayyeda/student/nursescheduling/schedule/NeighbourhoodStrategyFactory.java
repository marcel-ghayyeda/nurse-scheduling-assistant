package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.AdaptiveLargeNeighbourhoodStrategy.Adaptation;
import pl.edu.agh.ghayyeda.student.nursescheduling.solver.AlgorithmMetadata;

@Component
public class NeighbourhoodStrategyFactory {

    private static final Logger log = LoggerFactory.getLogger(NeighbourhoodStrategyFactory.class);

    public NeighbourhoodStrategy createNeighbourhoodStrategy(AlgorithmMetadata algorithmMetadata, ConstraintValidationResult constraintValidationResult) {
        if (algorithmMetadata.getProgressPercentage() > 60 || constraintValidationResult.getConstraintViolationsDescriptions().isEmpty()) {
            log.info("Using SimpleNeighbourhoodStrategy");
            return new SimpleNeighbourhoodStrategy();
        } else if (algorithmMetadata.getProgressPercentage() > 30) {
            log.info("Using AdaptiveLargeNeighbourhoodStrategy(WIDE)");
            return new AdaptiveLargeNeighbourhoodStrategy(Adaptation.WIDE);
        } else {
            log.info("Using AdaptiveLargeNeighbourhoodStrategy(NARROW)");
            return new AdaptiveLargeNeighbourhoodStrategy(Adaptation.NARROW);
        }
    }
}
