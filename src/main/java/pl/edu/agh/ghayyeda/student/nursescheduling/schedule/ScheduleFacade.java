package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationResult;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.penaltyaware.PenaltyAwareScheduleConstraintValidationFacade;
import pl.edu.agh.ghayyeda.student.nursescheduling.solver.SolverAccuracy;
import pl.edu.agh.ghayyeda.student.nursescheduling.solver.TabuSearchSolver;
import pl.edu.agh.ghayyeda.student.nursescheduling.util.ScheduleValidationUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Component
public class ScheduleFacade {

    private final ScheduleDao scheduleDao;
    private final PenaltyAwareScheduleConstraintValidationFacade penaltyAwareScheduleConstraintValidationFacade;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public ScheduleFacade(ScheduleDao scheduleDao, PenaltyAwareScheduleConstraintValidationFacade penaltyAwareScheduleConstraintValidationFacade) {
        this.scheduleDao = scheduleDao;
        this.penaltyAwareScheduleConstraintValidationFacade = penaltyAwareScheduleConstraintValidationFacade;
    }

    public List<ScheduleDescription> getLatestScheduleDescriptions() {
        return scheduleDao.getLatestSchedules()
                .stream()
                .map(this::toScheduleDescription)
                .collect(Collectors.toList());
    }

    public UUID save(Schedule schedule) {
        return scheduleDao.save(schedule);
    }

    public UUID save(Schedule schedule, String name) {
        return scheduleDao.save(schedule, name);
    }


    public UUID save(UUID id, Schedule schedule, String name) {
        return scheduleDao.save(id, schedule, name);
    }

    public Optional<ScheduleWrapper> getById(UUID id) {
        return scheduleDao.getById(id)
                .map(scheduleDto -> new ScheduleWrapper(toScheduleDescription(scheduleDto), scheduleDto.getSchedule()));
    }

    private ScheduleDescription toScheduleDescription(ScheduleDto scheduleDto) {
        var validationResult = penaltyAwareScheduleConstraintValidationFacade.validate(scheduleDto.getSchedule());
        return new ScheduleDescription(scheduleDto.getId(), scheduleDto.getName(), validationResult.isFeasible(), validationResult.getConstraintViolationsDescriptions());
    }

    public CompletableFuture<UUID> fixAsync(Schedule schedule, String newScheduleName, SolverAccuracy solverAccuracy) {
        var validationStartTime = ScheduleValidationUtils.getStandardValidationStartTime(schedule);
        var validationEndTime = ScheduleValidationUtils.getStandardValidationEndTime(schedule);
        var solver = new TabuSearchSolver(penaltyAwareScheduleConstraintValidationFacade, validationStartTime, validationEndTime, solverAccuracy);
        return supplyAsync(() -> solver.findFeasibleSchedule(schedule), executorService)
                .thenApply(foundSchedule -> save(foundSchedule, newScheduleName));
    }

    private boolean isFeasible(ScheduleDto scheduleDto) {
        return penaltyAwareScheduleConstraintValidationFacade.validate(scheduleDto.getSchedule()).isFeasible();
    }

}
