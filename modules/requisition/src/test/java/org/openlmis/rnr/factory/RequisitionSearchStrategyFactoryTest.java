/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;
import org.openlmis.rnr.strategy.FacilityDateRangeSearch;
import org.openlmis.rnr.strategy.FacilityProgramDateRangeSearch;
import org.openlmis.rnr.strategy.RequisitionSearchStrategy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({RequisitionSearchStrategyFactory.class})
@RunWith(PowerMockRunner.class)
public class RequisitionSearchStrategyFactoryTest {

  @Mock
  ProcessingScheduleService processingScheduleService;

  @Mock
  RequisitionRepository requisitionRepository;

  @Mock
  ProgramService programService;

  @InjectMocks
  RequisitionSearchStrategyFactory requisitionSearchStrategyFactory;

  @Test
  public void shouldGetSearchStrategyForFacilityProgramAndDateRange() throws Exception {
    Integer facilityId = 1, programId = 1;
    Date periodStartDate = new Date(), periodEndDate = new Date();
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facilityId, programId, periodStartDate, periodEndDate);

    RequisitionSearchStrategy facilityProgramDateRangeStrategy = requisitionSearchStrategyFactory.getSearchStrategy(criteria);

    assertTrue(facilityProgramDateRangeStrategy instanceof FacilityProgramDateRangeSearch);
  }

  @Test
  public void shouldGetSearchStrategyForFacilityAndDateRange() throws Exception {
    Integer facilityId = 1, programId = null;
    Date periodStartDate = new Date(), periodEndDate = new Date();
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facilityId, programId, periodStartDate, periodEndDate);
    whenNew(FacilityDateRangeSearch.class).withArguments(criteria, processingScheduleService, requisitionRepository, programService)
      .thenReturn(mock(FacilityDateRangeSearch.class));

    RequisitionSearchStrategy facilityDateRangeStrategy = requisitionSearchStrategyFactory.getSearchStrategy(criteria);

    assertTrue(facilityDateRangeStrategy instanceof FacilityDateRangeSearch);
    verifyNew(FacilityDateRangeSearch.class).withArguments(criteria, processingScheduleService, requisitionRepository, programService);
  }
}
