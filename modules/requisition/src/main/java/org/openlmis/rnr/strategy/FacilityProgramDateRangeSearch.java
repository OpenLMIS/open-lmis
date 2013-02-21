package org.openlmis.rnr.strategy;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;

import java.util.List;

@Data
@NoArgsConstructor
public class FacilityProgramDateRangeSearch implements RequisitionSearchStrategy {

  private ProcessingScheduleService processingScheduleService;
  private RequisitionRepository requisitionRepository;

  public FacilityProgramDateRangeSearch(ProcessingScheduleService processingScheduleService, RequisitionRepository requisitionRepository) {
    this.processingScheduleService = processingScheduleService;
    this.requisitionRepository = requisitionRepository;
  }

  @Override
  public List<Rnr> search(RequisitionSearchCriteria criteria) {
    Facility facility = new Facility(criteria.getFacilityId());
    Program program = new Program(criteria.getProgramId());
    List<ProcessingPeriod> periods = processingScheduleService.getAllPeriodsForDateRange(facility, program,
      criteria.getDateRangeStart(), criteria.getDateRangeEnd());

    return requisitionRepository.get(facility, program, periods);
  }
}
