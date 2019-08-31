package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.penaltyaware.PenaltyAwareScheduleConstraintValidationFacade;
import pl.edu.agh.ghayyeda.student.nursescheduling.solver.SolverAccuracy;
import pl.edu.agh.ghayyeda.student.nursescheduling.solver.TabuSearchSolver;
import pl.edu.agh.ghayyeda.student.nursescheduling.util.ScheduleValidationUtils;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toList;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.ScheduleBuilder.schedule;

@Component
public class ScheduleFacade {

    private final ScheduleDao scheduleDao;
    private final PenaltyAwareScheduleConstraintValidationFacade penaltyAwareScheduleConstraintValidationFacade;
    private final NeighbourhoodStrategyFactory neighbourhoodStrategyFactory;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public ScheduleFacade(ScheduleDao scheduleDao, PenaltyAwareScheduleConstraintValidationFacade penaltyAwareScheduleConstraintValidationFacade, NeighbourhoodStrategyFactory neighbourhoodStrategyFactory) {
        this.scheduleDao = scheduleDao;
        this.penaltyAwareScheduleConstraintValidationFacade = penaltyAwareScheduleConstraintValidationFacade;
        this.neighbourhoodStrategyFactory = neighbourhoodStrategyFactory;
    }

    public List<ScheduleDescription> getLatestScheduleDescriptions() {
        return scheduleDao.getAll()
                .stream()
                .map(this::toScheduleDescription)
                .collect(toList());
    }

    public UUID save(Schedule schedule) {
        return scheduleDao.save(schedule);
    }

    public UUID save(Schedule schedule, String name) {
        return scheduleDao.save(schedule, name);
    }

    public Schedule generateRandomSchedule(Year year, Month month, int numberOfChildren, int numberOfNurses) {
        List<List<Shift>> nurseShifts = IntStream.rangeClosed(1, numberOfNurses)
                .mapToObj(nurse -> IntStream.rangeClosed(1, month.length(year.isLeap())).mapToObj(__ -> randomShift()).collect(toList()))
                .collect(toList());

        return schedule()
                .forMonth(month)
                .forYear(year.getValue())
                .nursesShifts(nurseShifts)
                .numberOfChildren(numberOfChildren)
                .adjustForMonthLength()
                .build();
    }

    private Shift randomShift() {
        var shifts = Stream.of(Shift.DAY, Shift.NIGHT, Shift.DAY_OFF).collect(toList());
        return shifts.get(ThreadLocalRandom.current().nextInt(0, shifts.size()));
    }


    public UUID save(UUID id, Schedule schedule, String name) {
        return scheduleDao.save(id, schedule, name);
    }

    public void delete(UUID scheduleId) {
        scheduleDao.delete(scheduleId);
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
        var solver = new TabuSearchSolver(neighbourhoodStrategyFactory, penaltyAwareScheduleConstraintValidationFacade, validationStartTime, validationEndTime, solverAccuracy);
        return supplyAsync(() -> solver.findFeasibleSchedule(schedule), executorService)
                .thenApply(foundSchedule -> save(foundSchedule, newScheduleName));
    }

    private boolean isFeasible(ScheduleDto scheduleDto) {
        return penaltyAwareScheduleConstraintValidationFacade.validate(scheduleDto.getSchedule()).isFeasible();
    }
}
