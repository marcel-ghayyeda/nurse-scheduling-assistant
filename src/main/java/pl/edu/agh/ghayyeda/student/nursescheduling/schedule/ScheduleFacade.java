package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import org.springframework.stereotype.Component;
import pl.edu.agh.ghayyeda.student.nursescheduling.constraint.ScheduleConstraintValidationFacade;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ScheduleFacade {

    private final ScheduleDao scheduleDao;
    private final ScheduleConstraintValidationFacade scheduleConstraintValidationFacade;

    public ScheduleFacade(ScheduleDao scheduleDao, ScheduleConstraintValidationFacade scheduleConstraintValidationFacade) {
        this.scheduleDao = scheduleDao;
        this.scheduleConstraintValidationFacade = scheduleConstraintValidationFacade;
    }

    public List<ScheduleDescription> getLatestScheduleDescriptions() {
        return scheduleDao.getLatestSchedules()
                .stream()
                .map(this::toScheduleDescription)
                .collect(Collectors.toList());
    }

    private ScheduleDescription toScheduleDescription(ScheduleDto scheduleDto) {
        return new ScheduleDescription(scheduleDto.getId(), scheduleDto.getName(), isFeasible(scheduleDto));
    }

    public Optional<ScheduleWrapper> getById(UUID id) {
        return scheduleDao.getById(id)
                .map(scheduleDto -> new ScheduleWrapper(toScheduleDescription(scheduleDto), isFeasible(scheduleDto), scheduleDto.getSchedule()));
    }

    private boolean isFeasible(ScheduleDto scheduleDto) {
        return scheduleConstraintValidationFacade.validate(scheduleDto.getSchedule()).isFeasible();
    }


}
