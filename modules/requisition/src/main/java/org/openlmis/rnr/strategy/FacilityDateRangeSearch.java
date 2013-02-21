package org.openlmis.rnr.strategy;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;

import java.util.ArrayList;
import java.util.List;

import static org.openlmis.core.domain.Right.VIEW_REQUISITION;

@NoArgsConstructor
public class FacilityDateRangeSearch implements RequisitionSearchStrategy {

  private ProgramService programService;
  private ProcessingScheduleService processingScheduleService;
  private RequisitionRepository requisitionRepository;

  public FacilityDateRangeSearch(ProcessingScheduleService processingScheduleService, RequisitionRepository requisitionRepository, ProgramService programService) {
    this.programService = programService;
    this.processingScheduleService = processingScheduleService;
    this.requisitionRepository = requisitionRepository;
  }

  @Override
  public List<Rnr> search(RequisitionSearchCriteria criteria) {
    Facility facility = new Facility(criteria.getFacilityId());
    List<Program> programs = programService.getProgramsSupportedByFacilityForUserWithRights(criteria.getFacilityId(), criteria.getUserId(), VIEW_REQUISITION);
    List<Rnr> requisitions = new ArrayList<>();
    for (Program program : programs) {
      List<ProcessingPeriod> periods = processingScheduleService.getAllPeriodsForDateRange(facility, program, criteria.getDateRangeStart(), criteria.getDateRangeEnd());
      requisitions.addAll(requisitionRepository.get(facility, program, periods));
    }
    return requisitions;
  }
}
