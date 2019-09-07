package pl.edu.agh.ghayyeda.student.nursescheduling.schedule.neighbourhood;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.solver.AlgorithmMetadata;

import java.util.List;

@Component
public class NeighbourhoodStrategyFactory {

    private static final Logger log = LoggerFactory.getLogger(NeighbourhoodStrategyFactory.class);
    private final SimpleNeighbourhoodStrategy simpleNeighbourhoodStrategy = new SimpleNeighbourhoodStrategy();
    private final SimpleNeighbourhoodStrategy randomSimpleNeighbourhoodStrategy = new SimpleNeighbourhoodStrategy(0.05);
    private final AdaptiveLargeNeighbourhoodStrategy adaptiveWideNeighbourhood = new AdaptiveLargeNeighbourhoodStrategy(Adaptation.WIDE);
    private final AdaptiveLargeNeighbourhoodStrategy adaptiveNarrowNeighbourhood = new AdaptiveLargeNeighbourhoodStrategy(Adaptation.NARROW);
    private final RandomlyAdaptiveLargeNeighbourhoodStrategy randomlyAdaptiveWideNeighbourhood = new RandomlyAdaptiveLargeNeighbourhoodStrategy(Adaptation.WIDE);
    private final RandomlyAdaptiveLargeNeighbourhoodStrategy randomlyAdaptiveNarrowNeighbourhood = new RandomlyAdaptiveLargeNeighbourhoodStrategy(Adaptation.NARROW);

    public NeighbourhoodStrategy createNeighbourhoodStrategy(AlgorithmMetadata algorithmMetadata, ConstraintValidationResult constraintValidationResult) {
        if (algorithmMetadata.getLatestPenalties(16).map(this::qualityOfCandidatesDidNotChangeIn).orElse(false) || constraintValidationResult.getConstraintViolationsDescriptions().isEmpty()) {
            log.info("Using SimpleNeighbourhoodStrategy");
            return new SimpleNeighbourhoodStrategy();
        } else if (algorithmMetadata.getLatestPenalties(8).map(this::qualityOfCandidatesDidNotChangeIn).orElse(false)) {
            log.info("Using AdaptiveLargeNeighbourhoodStrategy(WIDE)");
            return adaptiveWideNeighbourhood;
        } else if (algorithmMetadata.getLatestPenalties(4).map(this::qualityOfCandidatesDidNotChangeIn).orElse(false)) {
            log.info("Using AdaptiveLargeNeighbourhoodStrategy(NARROW)");
            return adaptiveNarrowNeighbourhood;
        } else if (algorithmMetadata.getLatestPenalties(2).map(this::qualityOfCandidatesDidNotChangeIn).orElse(false)) {
            log.info("Using RandomlyAdaptiveLargeNeighbourhoodStrategy(WIDE)");
            return randomlyAdaptiveWideNeighbourhood;
        } else {
            log.info("Using RandomlyAdaptiveLargeNeighbourhoodStrategy(NARROW)");
            return randomlyAdaptiveNarrowNeighbourhood;
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

    public NeighbourhoodStrategy createRandomSimpleNeighbourhoodStrategy() {
        return randomSimpleNeighbourhoodStrategy;
    }

    public NeighbourhoodStrategy createAdaptiveNeighbourhoodStrategy(Adaptation adaptation) {
        switch (adaptation) {
            case WIDE:
                return adaptiveWideNeighbourhood;
            case NARROW:
                return adaptiveNarrowNeighbourhood;
            default:
                throw new IllegalStateException();
        }
    }


}
