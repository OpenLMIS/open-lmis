package org.openlmis.rnr.strategy;

import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;

import java.util.List;

public interface RequisitionSearchStrategy {
  public List<Rnr> search(RequisitionSearchCriteria criteria);
}
