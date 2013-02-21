package org.openlmis.rnr.factory;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;
import org.openlmis.rnr.strategy.FacilityDateRangeSearch;
import org.openlmis.rnr.strategy.FacilityProgramDateRangeSearch;
import org.openlmis.rnr.strategy.RequisitionSearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class RequisitionFactory {

  private ProcessingScheduleService processingScheduleService;
  private RequisitionRepository requisitionRepository;
  private ProgramService programService;

  @Autowired
  public RequisitionFactory(ProcessingScheduleService processingScheduleService, RequisitionRepository requisitionRepository, ProgramService programService) {
    this.processingScheduleService = processingScheduleService;
    this.requisitionRepository = requisitionRepository;
    this.programService = programService;
  }

  public RequisitionSearchStrategy getSearchStrategy(RequisitionSearchCriteria criteria) {
    if (criteria.getProgramId() == null){
      return new FacilityDateRangeSearch(processingScheduleService, requisitionRepository, programService);
    }
    return new FacilityProgramDateRangeSearch(processingScheduleService, requisitionRepository);

  }
}
