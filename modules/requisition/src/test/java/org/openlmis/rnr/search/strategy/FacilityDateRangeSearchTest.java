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

import java.text.SimpleDateFormat;
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
import static org.openlmis.core.domain.RightName.VIEW_REQUISITION;
import static org.openlmis.rnr.builder.RequisitionSearchCriteriaBuilder.*;

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

  String dateRangeStart, dateRangeEnd;
  Facility facility;
  Long userId;
  FacilityDateRangeSearch strategy;


  @Before
  public void setUp() throws Exception {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    dateRangeStart = dateFormat.format(new Date());
    dateRangeEnd = dateFormat.format(new Date());
    Long facilityId = 1L, programId = null;
    facility = new Facility(facilityId);
    userId = 1L;

    RequisitionSearchCriteria criteria = make(a(defaultSearchCriteria,
      with(facilityIdProperty, facilityId),
      with(programIdProperty, programId),
      with(userIdProperty, userId),
      with(startDate, dateRangeStart),
      with(endDate, dateRangeEnd)));
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

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    Date startDate = dateFormat.parse(dateRangeStart);
    Date endDate = dateFormat.parse(dateRangeEnd);
    List<Rnr> requisitions = new ArrayList<>();
    List<ProcessingPeriod> periodsForProgram1 = new ArrayList<>();
    List<ProcessingPeriod> periodsForProgram2 = new ArrayList<>();
    ArrayList<Rnr> requisitionsForProgram2 = new ArrayList<>();
    when(programService.getProgramsForUserByFacilityAndRights(1L, 1L, VIEW_REQUISITION)).thenReturn(programs);
    when(scheduleService.getUsedPeriodsForDateRange(facility, program1, startDate, endDate)).thenReturn(periodsForProgram1);
    when(scheduleService.getUsedPeriodsForDateRange(facility, program2, startDate, endDate)).thenReturn(periodsForProgram2);
    when(repository.getPostSubmitRequisitions(facility, program1, periodsForProgram1)).thenReturn(requisitions);
    when(repository.getPostSubmitRequisitions(facility, program2, periodsForProgram2)).thenReturn(requisitionsForProgram2);
    when(requisitionPermissionService.hasPermission(userId, facility, program1, VIEW_REQUISITION)).thenReturn(true);
    when(requisitionPermissionService.hasPermission(userId, facility, program2, VIEW_REQUISITION)).thenReturn(true);

    List<Rnr> actualRequisitions = strategy.search();

    requisitions.addAll(requisitionsForProgram2);
    assertThat(actualRequisitions, is(requisitions));
    verify(programService).getProgramsForUserByFacilityAndRights(1L, 1L, VIEW_REQUISITION);
    verify(scheduleService).getUsedPeriodsForDateRange(facility, program1, startDate, endDate);
    verify(scheduleService).getUsedPeriodsForDateRange(facility, program2, startDate, endDate);
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
