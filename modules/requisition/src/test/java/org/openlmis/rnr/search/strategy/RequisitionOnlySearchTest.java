/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.VIEW_REQUISITION;

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

    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facilityId, programId, periodId);
    criteria.setUserId(userId);

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

    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facilityId, programId, periodId);
    criteria.setUserId(userId);
    RequisitionOnlySearch requisitionOnlySearch = new RequisitionOnlySearch(criteria, requisitionPermissionService, requisitionRepository);
    when(requisitionPermissionService.hasPermission(userId, facility, program, VIEW_REQUISITION)).thenReturn(false);

    assertThat(requisitionOnlySearch.search().size(), is(0));

    verify(requisitionRepository, never()).getRequisitionWithoutLineItems(anyLong(), anyLong(), anyLong());
  }
}
