package org.openlmis.rnr.factory;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;
import org.openlmis.rnr.strategy.FacilityProgramDateRangeSearch;
import org.openlmis.rnr.strategy.RequisitionSearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class RequisitionFactory {

  private ProcessingScheduleService processingScheduleService;
  private RequisitionRepository requisitionRepository;

  @Autowired
  public RequisitionFactory(ProcessingScheduleService processingScheduleService, RequisitionRepository requisitionRepository) {
    this.processingScheduleService = processingScheduleService;
    this.requisitionRepository = requisitionRepository;
  }

  public RequisitionSearchStrategy getSearchStrategy(RequisitionSearchCriteria criteria) {
    return new FacilityProgramDateRangeSearch(processingScheduleService, requisitionRepository);
  }
}
