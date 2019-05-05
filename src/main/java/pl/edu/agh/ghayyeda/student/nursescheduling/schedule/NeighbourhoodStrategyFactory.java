package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.springframework.stereotype.Component;

@Component
public class NeighbourhoodStrategyFactory {

    public NeighbourhoodStrategy createNeighbourhoodStrategy() {
        return new FullNeighbourhoodStrategy();
    }
}
