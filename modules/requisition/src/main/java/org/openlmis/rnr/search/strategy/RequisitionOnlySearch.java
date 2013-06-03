package org.openlmis.rnr.search.strategy;

import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;

import java.util.List;

import static java.util.Arrays.asList;

public class RequisitionOnlySearch implements RequisitionSearchStrategy {
  private RequisitionSearchCriteria criteria;
  private RequisitionRepository requisitionRepository;

  public RequisitionOnlySearch(RequisitionSearchCriteria criteria, RequisitionRepository requisitionRepository) {
    this.criteria = criteria;
    this.requisitionRepository = requisitionRepository;
  }

  @Override
  public List<Rnr> search() {
    return asList(requisitionRepository.getRequisitionWithoutLineItems(criteria.getFacilityId(),
        criteria.getProgramId(),
        criteria.getPeriodId()));

  }
}
