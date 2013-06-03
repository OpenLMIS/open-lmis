/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.search.strategy;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.Right.VIEW_REQUISITION;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityDateRangeSearchTest {

  @Mock
  ProgramService programService;

  @Mock
  RequisitionRepository requisitionRepository;

  @Mock
  ProcessingScheduleService processingScheduleService;


  @Test
  public void shouldSearchRequisitionsWithFacilityAndDateRange() throws Exception {
    //Arrange
    Date dateRangeStart = new Date(), dateRangeEnd = new Date();
    Long facilityId = 1L, programId = null, userId = 1L;
    Facility facility = new Facility(facilityId);
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facilityId, programId, userId, dateRangeStart, dateRangeEnd);
    FacilityDateRangeSearch strategy = new FacilityDateRangeSearch(criteria, processingScheduleService, requisitionRepository, programService);

    final Program program1 = make(a(ProgramBuilder.defaultProgram));
    final Program program2 = make(a(ProgramBuilder.defaultProgram, with(ProgramBuilder.programCode, "My Program")));
    List<Program> programs = new ArrayList<Program>() {{
      add(program1);
      add(program2);
    }};
    List<Rnr> requisitions = new ArrayList<>();
    List<ProcessingPeriod> periodsForProgram1 = new ArrayList<>();
    List<ProcessingPeriod> periodsForProgram2 = new ArrayList<>();
    ArrayList<Rnr> requisitionsForProgram2 = new ArrayList<>();
    when(programService.getProgramsForUserByFacilityAndRights(1L, 1L, VIEW_REQUISITION)).thenReturn(programs);
    when(processingScheduleService.getAllPeriodsForDateRange(facility, program1, dateRangeStart, dateRangeEnd)).thenReturn(periodsForProgram1);
    when(processingScheduleService.getAllPeriodsForDateRange(facility, program2, dateRangeStart, dateRangeEnd)).thenReturn(periodsForProgram2);
    when(requisitionRepository.get(facility, program1, periodsForProgram1)).thenReturn(requisitions);
    when(requisitionRepository.get(facility, program2, periodsForProgram2)).thenReturn(requisitionsForProgram2);

    //Act
    List<Rnr> actualRequisitions = strategy.search();

    //Assert
    requisitions.addAll(requisitionsForProgram2);
    assertThat(actualRequisitions, is(requisitions));
    verify(programService).getProgramsForUserByFacilityAndRights(1L, 1L, VIEW_REQUISITION);
    verify(processingScheduleService).getAllPeriodsForDateRange(facility, program1, dateRangeStart, dateRangeEnd);
    verify(processingScheduleService).getAllPeriodsForDateRange(facility, program2, dateRangeStart, dateRangeEnd);
    verify(requisitionRepository).get(facility, program1, periodsForProgram1);
    verify(requisitionRepository).get(facility, program2, periodsForProgram2);
  }
}
