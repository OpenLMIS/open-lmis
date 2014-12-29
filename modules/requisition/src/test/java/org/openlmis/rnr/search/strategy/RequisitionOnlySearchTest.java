/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.search.strategy;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionPermissionService;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.RightName.VIEW_REQUISITION;
import static org.openlmis.rnr.builder.RequisitionSearchCriteriaBuilder.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RequisitionOnlySearchTest {

  @Mock
  RequisitionRepository requisitionRepository;

  @Mock
  private RequisitionPermissionService requisitionPermissionService;

  @Test
  public void testSearch() throws Exception {
    Long facilityId = 1L, programId = 1L, periodId = 4L, userId = 1L;
    Facility facility = new Facility(facilityId);
    Program program = new Program(programId);

    RequisitionSearchCriteria criteria = make(a(defaultSearchCriteria,
      with(facilityIdProperty, facilityId),
      with(programIdProperty, programId),
      with(periodIdProperty, periodId),
      with(userIdProperty, userId)));

    RequisitionOnlySearch requisitionOnlySearch = new RequisitionOnlySearch(criteria, requisitionPermissionService, requisitionRepository);
    Rnr requisition = new Rnr();
    when(requisitionRepository.getRequisitionWithoutLineItems(facilityId, programId, periodId)).thenReturn(requisition);
    when(requisitionPermissionService.hasPermission(userId, facility, program, VIEW_REQUISITION)).thenReturn(true);

    List<Rnr> actualRequisitions = requisitionOnlySearch.search();

    assertThat(actualRequisitions, is(asList(requisition)));
    verify(requisitionRepository).getRequisitionWithoutLineItems(facilityId, programId, periodId);
  }

  @Test
  public void shouldNotSearchIfUserDoesNotHavePermission() throws Exception {
    Long facilityId = 1L, programId = 1L, periodId = 4L, userId = 1L;
    Facility facility = new Facility(facilityId);
    Program program = new Program(programId);

    RequisitionSearchCriteria criteria = make(a(defaultSearchCriteria,
      with(facilityIdProperty, facilityId),
      with(programIdProperty, programId),
      with(periodIdProperty, periodId),
      with(userIdProperty, userId)));

    RequisitionOnlySearch requisitionOnlySearch = new RequisitionOnlySearch(criteria, requisitionPermissionService, requisitionRepository);
    when(requisitionPermissionService.hasPermission(userId, facility, program, VIEW_REQUISITION)).thenReturn(false);

    assertThat(requisitionOnlySearch.search().size(), is(0));

    verify(requisitionRepository, never()).getRequisitionWithoutLineItems(anyLong(), anyLong(), anyLong());
  }
}
