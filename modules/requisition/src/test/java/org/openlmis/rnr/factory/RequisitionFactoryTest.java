package org.openlmis.rnr.factory;

import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;
import org.openlmis.rnr.strategy.FacilityProgramDateRangeSearch;
import org.openlmis.rnr.strategy.RequisitionSearchStrategy;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RequisitionFactoryTest {

  @Mock
  ProcessingScheduleService processingScheduleService;

  @Mock
  RequisitionRepository requisitionRepository;

  @Test
  public void shouldGetSearchStrategyForFacilityProgramAndDateRange() throws Exception {
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(1, 1, new Date(), new Date());

    RequisitionFactory requisitionFactory = new RequisitionFactory(processingScheduleService, requisitionRepository);

    RequisitionSearchStrategy facilityProgramDateRangeStrategy = requisitionFactory.getSearchStrategy(criteria);

    assertThat(facilityProgramDateRangeStrategy instanceof FacilityProgramDateRangeSearch, is(true));
  }
}
