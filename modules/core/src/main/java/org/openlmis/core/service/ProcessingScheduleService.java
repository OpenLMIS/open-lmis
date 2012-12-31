package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.repository.ProcessingScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class ProcessingScheduleService {
    private ProcessingScheduleRepository repository;

    @Autowired
    public ProcessingScheduleService(ProcessingScheduleRepository repository) {
        this.repository = repository;
    }

    public List<ProcessingSchedule> getAll() {
        return repository.getAll();
    }

    public void save(ProcessingSchedule processingSchedule) {
        repository.save(processingSchedule);
    }
}
