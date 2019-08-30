package pl.edu.agh.ghayyeda.student.nursescheduling.solver;

import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.newSetFromMap;

class TabuSet {
    private static final int TABU_LIST_SIZE = 50;

    static Set<Schedule> newInstance() {
        return newSetFromMap(new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Schedule, Boolean> eldest) {
                return size() > TABU_LIST_SIZE;
            }
        });
    }
}
