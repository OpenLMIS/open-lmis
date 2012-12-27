package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.repository.ProcessingScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class ProcessingScheduleService {
    private ProcessingScheduleRepository processingScheduleRepository;

    public ProcessingScheduleService(ProcessingScheduleRepository processingScheduleRepository) {
        this.processingScheduleRepository = processingScheduleRepository;
    }

    public List<ProcessingSchedule> getAll() {
        return processingScheduleRepository.getAll();
    }
}
