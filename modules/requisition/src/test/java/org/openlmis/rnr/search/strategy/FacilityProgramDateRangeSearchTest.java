/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.search.strategy;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionPermissionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.Right.VIEW_REQUISITION;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityProgramDateRangeSearchTest {

  @Mock
  ProcessingScheduleService processingScheduleService;

  @Mock
  RequisitionRepository requisitionRepository;

  @Mock
  RequisitionPermissionService requisitionPermissionService;

  Date dateRangeStart, dateRangeEnd;
  Long facilityId, programId, userId;

  Facility facility = new Facility(facilityId);
  Program program = new Program(programId);
  FacilityProgramDateRangeSearch facilityProgramDateRangeSearch;

  @Before
  public void setUp() throws Exception {
    dateRangeStart = new Date();
    dateRangeEnd = new Date();
    facilityId = 1L;
    programId = 1L;
    userId = 100L;
    facility = new Facility(facilityId);
    program = new Program(programId);

    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facilityId, programId, dateRangeStart, dateRangeEnd);
    criteria.setUserId(userId);

    facilityProgramDateRangeSearch = new FacilityProgramDateRangeSearch(criteria,
      requisitionPermissionService, processingScheduleService, requisitionRepository);
  }

  @Test
  public void testSearch() throws Exception {
    List<ProcessingPeriod> periods = new ArrayList<>();
    List<Rnr> requisitions = new ArrayList<>();

    when(processingScheduleService.getAllPeriodsForDateRange(facility, program, dateRangeStart, dateRangeEnd)).thenReturn(periods);
    when(requisitionRepository.getPostSubmitRequisitions(facility, program, periods)).thenReturn(requisitions);
    when(requisitionPermissionService.hasPermission(userId, facility, program, VIEW_REQUISITION)).thenReturn(true);


    List<Rnr> actualRequisitions = facilityProgramDateRangeSearch.search();

    assertThat(actualRequisitions, is(requisitions));
    verify(processingScheduleService).getAllPeriodsForDateRange(facility, program, dateRangeStart, dateRangeEnd);
    verify(requisitionRepository).getPostSubmitRequisitions(facility, program, periods);
  }

  @Test
  public void shouldNotBeSearchableIfThereIsNoPermission() throws Exception {
    when(requisitionPermissionService.hasPermission(userId, facility, program, VIEW_REQUISITION)).thenReturn(false);

    boolean permissible = facilityProgramDateRangeSearch.isSearchable(VIEW_REQUISITION);

    assertThat(permissible, is(false));
    verify(requisitionPermissionService).hasPermission(userId, facility, program, VIEW_REQUISITION);

  }

  @Test
  public void shouldBeSearchableIfPermissionIsThere() throws Exception {
    when(requisitionPermissionService.hasPermission(userId, facility, program, VIEW_REQUISITION)).thenReturn(true);

    boolean permissible = facilityProgramDateRangeSearch.isSearchable(VIEW_REQUISITION);

    assertThat(permissible, is(true));
    verify(requisitionPermissionService).hasPermission(userId, facility, program, VIEW_REQUISITION);

  }
}
