package org.openlmis.rnr.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;
import org.openlmis.rnr.strategy.FacilityDateRangeSearch;
import org.openlmis.rnr.strategy.FacilityProgramDateRangeSearch;
import org.openlmis.rnr.strategy.RequisitionSearchStrategy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

@PrepareForTest({RequisitionFactory.class})
@RunWith(PowerMockRunner.class)
public class RequisitionFactoryTest {

  @Mock
  ProcessingScheduleService processingScheduleService;

  @Mock
  RequisitionRepository requisitionRepository;

  @Mock
  ProgramService programService;

  @Test
  public void shouldGetSearchStrategyForFacilityProgramAndDateRange() throws Exception {
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(1, 1, new Date(), new Date());

    RequisitionFactory requisitionFactory = new RequisitionFactory(processingScheduleService, requisitionRepository, null);

    RequisitionSearchStrategy facilityProgramDateRangeStrategy = requisitionFactory.getSearchStrategy(criteria);

    assertThat(facilityProgramDateRangeStrategy instanceof FacilityProgramDateRangeSearch, is(true));
  }

  @Test
  public void shouldGetSearchStrategyForFacilityAndDateRange() throws Exception {
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(1, null, new Date(), new Date());
    PowerMockito.whenNew(FacilityDateRangeSearch.class).withArguments(processingScheduleService, requisitionRepository, programService)
      .thenReturn(mock(FacilityDateRangeSearch.class));
    RequisitionFactory requisitionFactory = new RequisitionFactory(processingScheduleService, requisitionRepository, programService);

    RequisitionSearchStrategy facilityDateRangeStrategy = requisitionFactory.getSearchStrategy(criteria);

    assertThat(facilityDateRangeStrategy instanceof FacilityDateRangeSearch, is(true));
    PowerMockito.verifyNew(FacilityDateRangeSearch.class).withArguments(processingScheduleService, requisitionRepository, programService);
  }
}
