package pl.edu.agh.ghayyeda.student.nursescheduling.solver;

import pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Schedule;

public interface Solver {
    Schedule findFeasibleSchedule(Schedule schedule);
}
