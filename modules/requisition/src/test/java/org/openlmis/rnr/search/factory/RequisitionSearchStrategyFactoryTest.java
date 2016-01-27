/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.search.factory;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.search.strategy.*;
import org.openlmis.rnr.service.RequisitionPermissionService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertTrue;
import static org.openlmis.rnr.builder.RequisitionSearchCriteriaBuilder.*;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({RequisitionSearchStrategyFactory.class})
public class RequisitionSearchStrategyFactoryTest {

  @Mock
  ProcessingScheduleService processingScheduleService;

  @Mock
  RequisitionRepository requisitionRepository;

  @Mock
  ProgramService programService;

  @Mock
  private RequisitionPermissionService requisitionPermissionService;

  @InjectMocks
  RequisitionSearchStrategyFactory requisitionSearchStrategyFactory;

  @Test
  public void shouldGetSearchStrategyForFacilityProgramAndDateRange() throws Exception {
    Long facilityId = 1L, programId = 1L;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    RequisitionSearchCriteria criteria = make(a(defaultSearchCriteria,
      with(facilityIdProperty, facilityId),
      with(programIdProperty, programId),
      with(startDate, dateFormat.format(new Date())),
      with(endDate, dateFormat.format(new Date()))));
    ;
    RequisitionSearchStrategy facilityProgramDateRangeStrategy = requisitionSearchStrategyFactory.getSearchStrategy(criteria);

    assertTrue(facilityProgramDateRangeStrategy instanceof FacilityProgramDateRangeSearch);
  }

  @Test
  public void shouldGetSearchStrategyForFacilityAndDateRange() throws Exception {
    Long facilityId = 1L, programId = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    RequisitionSearchCriteria criteria = make(a(defaultSearchCriteria,
      with(facilityIdProperty, facilityId),
      with(programIdProperty, programId),
      with(startDate, dateFormat.format(new Date())),
      with(endDate, dateFormat.format(new Date()))));

    whenNew(FacilityDateRangeSearch.class)
      .withArguments(criteria, requisitionPermissionService, processingScheduleService, requisitionRepository, programService)
      .thenReturn(mock(FacilityDateRangeSearch.class));

    RequisitionSearchStrategy facilityDateRangeStrategy = requisitionSearchStrategyFactory.getSearchStrategy(criteria);

    assertTrue(facilityDateRangeStrategy instanceof FacilityDateRangeSearch);
    verifyNew(FacilityDateRangeSearch.class)
      .withArguments(criteria, requisitionPermissionService, processingScheduleService, requisitionRepository, programService);
  }

  @Test
  public void shouldUseRequisitionOnlyStrategyIfLineItemsAreNotRequired() throws Exception {
    Long facilityId = 1L, programId = null, periodId = 4L;
    RequisitionSearchCriteria criteria = make(a(defaultSearchCriteria,
      with(facilityIdProperty, facilityId),
      with(programIdProperty, programId),
      with(periodIdProperty, periodId),
      with(withoutLineItemFlag, true)));

    whenNew(RequisitionOnlySearch.class)
      .withArguments(criteria, requisitionPermissionService, requisitionRepository)
      .thenReturn(mock(RequisitionOnlySearch.class));

    RequisitionSearchStrategy facilityDateRangeStrategy = requisitionSearchStrategyFactory.getSearchStrategy(criteria);

    assertTrue(facilityDateRangeStrategy instanceof RequisitionOnlySearch);
    verifyNew(RequisitionOnlySearch.class).withArguments(criteria, requisitionPermissionService, requisitionRepository);
  }

  @Test
  public void shouldUseEmergencyRequisitionSearchStrategyIfEmergencyIsTrue() throws Exception {
    Long facilityId = 1L, programId = 3L, periodId = 4L;

    RequisitionSearchCriteria criteria = make(a(defaultSearchCriteria,
      with(facilityIdProperty, facilityId),
      with(programIdProperty, programId),
      with(periodIdProperty, periodId),
      with(withoutLineItemFlag, true)));

    criteria.setEmergency(true);
    whenNew(EmergencyRequisitionSearch.class)
      .withArguments(criteria, requisitionPermissionService, requisitionRepository)
      .thenReturn(mock(EmergencyRequisitionSearch.class));

    RequisitionSearchStrategy emergencyRequisitionSearch = requisitionSearchStrategyFactory.getSearchStrategy(criteria);

    assertTrue(emergencyRequisitionSearch instanceof EmergencyRequisitionSearch);
    verifyNew(EmergencyRequisitionSearch.class)
      .withArguments(criteria, requisitionPermissionService, requisitionRepository);
  }
}
