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
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionPermissionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.domain.Right.VIEW_REQUISITION;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityDateRangeSearchTest {

  @Mock
  ProgramService programService;

  @Mock
  RequisitionRepository repository;

  @Mock
  ProcessingScheduleService scheduleService;

  @Mock
  RequisitionPermissionService requisitionPermissionService;

  Date dateRangeStart, dateRangeEnd;
  Facility facility;
  Long userId;
  FacilityDateRangeSearch strategy;


  @Before
  public void setUp() throws Exception {
    dateRangeStart = new Date();
    dateRangeEnd = new Date();
    Long facilityId = 1L, programId = null;
    facility = new Facility(facilityId);
    userId = 1L;

    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facilityId, programId, userId, dateRangeStart, dateRangeEnd);
    strategy = new FacilityDateRangeSearch(criteria, requisitionPermissionService, scheduleService,
      repository,
      programService);


  }

  @Test
  public void shouldSearchRequisitionsWithFacilityAndDateRange() throws Exception {

    final Program program1 = make(a(defaultProgram));
    final Program program2 = make(a(defaultProgram, with(programCode, "My Program")));
    List<Program> programs = new ArrayList<Program>() {{
      add(program1);
      add(program2);
    }};

    List<Rnr> requisitions = new ArrayList<>();
    List<ProcessingPeriod> periodsForProgram1 = new ArrayList<>();
    List<ProcessingPeriod> periodsForProgram2 = new ArrayList<>();
    ArrayList<Rnr> requisitionsForProgram2 = new ArrayList<>();
    when(programService.getProgramsForUserByFacilityAndRights(1L, 1L, VIEW_REQUISITION)).thenReturn(programs);
    when(scheduleService.getAllPeriodsForDateRange(facility, program1, dateRangeStart, dateRangeEnd)).thenReturn(periodsForProgram1);
    when(scheduleService.getAllPeriodsForDateRange(facility, program2, dateRangeStart, dateRangeEnd)).thenReturn(periodsForProgram2);
    when(repository.getPostSubmitRequisitions(facility, program1, periodsForProgram1)).thenReturn(requisitions);
    when(repository.getPostSubmitRequisitions(facility, program2, periodsForProgram2)).thenReturn(requisitionsForProgram2);
    when(requisitionPermissionService.hasPermission(userId, facility, program1, VIEW_REQUISITION)).thenReturn(true);
    when(requisitionPermissionService.hasPermission(userId, facility, program2, VIEW_REQUISITION)).thenReturn(true);

    List<Rnr> actualRequisitions = strategy.search();

    requisitions.addAll(requisitionsForProgram2);
    assertThat(actualRequisitions, is(requisitions));
    verify(programService).getProgramsForUserByFacilityAndRights(1L, 1L, VIEW_REQUISITION);
    verify(scheduleService).getAllPeriodsForDateRange(facility, program1, dateRangeStart, dateRangeEnd);
    verify(scheduleService).getAllPeriodsForDateRange(facility, program2, dateRangeStart, dateRangeEnd);
    verify(repository).getPostSubmitRequisitions(facility, program1, periodsForProgram1);
    verify(repository).getPostSubmitRequisitions(facility, program2, periodsForProgram2);
    verify(requisitionPermissionService).hasPermission(userId, facility, program1, VIEW_REQUISITION);
    verify(requisitionPermissionService).hasPermission(userId, facility, program2, VIEW_REQUISITION);
  }

  @Test
  public void shouldNotReturnRequisitionIfUserDoesNotHaveViewPermission() throws Exception {

    final Program program1 = make(a(defaultProgram));
    List<Program> programs = new ArrayList<Program>() {{
      add(program1);
    }};

    when(programService.getProgramsForUserByFacilityAndRights(1L, 1L, VIEW_REQUISITION)).thenReturn(programs);
    when(requisitionPermissionService.hasPermission(userId, facility, program1, VIEW_REQUISITION)).thenReturn(false);

    assertThat(strategy.search().size(), is(0));

    verify(scheduleService, never()).getAllPeriodsForDateRange(any(Facility.class), any(Program.class), any(Date.class), any(Date.class));
    verify(repository, never()).getPostSubmitRequisitions(any(Facility.class), any(Program.class), any(List.class));

  }
}
