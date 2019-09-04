package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.AdaptiveLargeNeighbourhoodStrategy.Adaptation;
import pl.edu.agh.ghayyeda.student.nursescheduling.solver.AlgorithmMetadata;

import java.util.List;

@Component
public class NeighbourhoodStrategyFactory {

    private static final Logger log = LoggerFactory.getLogger(NeighbourhoodStrategyFactory.class);
    private final SimpleNeighbourhoodStrategy simpleNeighbourhoodStrategy = new SimpleNeighbourhoodStrategy();
    private final AdaptiveLargeNeighbourhoodStrategy wideNeighbourhood = new AdaptiveLargeNeighbourhoodStrategy(Adaptation.WIDE);
    private final AdaptiveLargeNeighbourhoodStrategy narrowNeighbourhood = new AdaptiveLargeNeighbourhoodStrategy(Adaptation.NARROW);

    public NeighbourhoodStrategy createNeighbourhoodStrategy(AlgorithmMetadata algorithmMetadata, ConstraintValidationResult constraintValidationResult) {
        if (algorithmMetadata.getLatestPenalties(150).map(this::qualityOfCandidatesDidNotChangeIn).orElse(false) || constraintValidationResult.getConstraintViolationsDescriptions().isEmpty()) {
            log.info("Using SimpleNeighbourhoodStrategy");
            return new SimpleNeighbourhoodStrategy();
        } else if (algorithmMetadata.getLatestPenalties(100).map(this::qualityOfCandidatesDidNotChangeIn).orElse(false)) {
            log.info("Using AdaptiveLargeNeighbourhoodStrategy(WIDE)");
            return wideNeighbourhood;
        } else {
            log.info("Using AdaptiveLargeNeighbourhoodStrategy(NARROW)");
            return narrowNeighbourhood;
        }
    }

    private boolean qualityOfCandidatesDidNotChangeIn(List<Double> latestPenalties) {
        if (latestPenalties.size() < 2) {
            return false;
        }
        double oldest = latestPenalties.get(latestPenalties.size() - 1);
        double newest = latestPenalties.get(0);
        return newest >= oldest || oldest - newest < 0.00001;
    }

    public NeighbourhoodStrategy createSimpleNeighbourhoodStrategy() {
        return simpleNeighbourhoodStrategy;
    }

    public NeighbourhoodStrategy createAdaptiveNeighbourhoodStrategy(Adaptation adaptation) {
        switch (adaptation) {
            case WIDE:
                return wideNeighbourhood;
            case NARROW:
                return narrowNeighbourhood;
            default:
                throw new IllegalStateException();
        }
    }


}
