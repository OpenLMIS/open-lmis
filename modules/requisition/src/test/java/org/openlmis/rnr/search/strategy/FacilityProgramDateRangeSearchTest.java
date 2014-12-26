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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.RightName.VIEW_REQUISITION;
import static org.openlmis.rnr.builder.RequisitionSearchCriteriaBuilder.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityProgramDateRangeSearchTest {

  @Mock
  ProcessingScheduleService processingScheduleService;

  @Mock
  RequisitionRepository requisitionRepository;

  @Mock
  RequisitionPermissionService requisitionPermissionService;

  String stringRangeStartDate, stringDateEndDate;
  Long facilityId, programId, userId;
  Date dateRangeStart, dateRangeEnd;
  Facility facility = new Facility(facilityId);
  Program program = new Program(programId);
  FacilityProgramDateRangeSearch facilityProgramDateRangeSearch;

  @Before
  public void setUp() throws Exception {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    stringRangeStartDate = dateFormat.format(new Date());
    stringDateEndDate = dateFormat.format(new Date());
    dateRangeStart = dateFormat.parse(stringRangeStartDate);
    dateRangeEnd = dateFormat.parse(stringDateEndDate);

    facilityId = 1L;
    programId = 1L;
    userId = 100L;
    facility = new Facility(facilityId);
    program = new Program(programId);

    RequisitionSearchCriteria criteria = make(a(defaultSearchCriteria,
      with(facilityIdProperty, facilityId),
      with(programIdProperty, programId),
      with(startDate, stringRangeStartDate),
      with(endDate, stringDateEndDate)));
    criteria.setUserId(userId);

    facilityProgramDateRangeSearch = new FacilityProgramDateRangeSearch(criteria,
      requisitionPermissionService, processingScheduleService, requisitionRepository);
  }

  @Test
  public void testSearch() throws Exception {
    List<ProcessingPeriod> periods = new ArrayList<>();
    List<Rnr> requisitions = new ArrayList<>();

    when(processingScheduleService.getUsedPeriodsForDateRange(facility, program, dateRangeStart, dateRangeEnd)).thenReturn(periods);
    when(requisitionRepository.getPostSubmitRequisitions(facility, program, periods)).thenReturn(requisitions);
    when(requisitionPermissionService.hasPermission(userId, facility, program, VIEW_REQUISITION)).thenReturn(true);


    List<Rnr> actualRequisitions = facilityProgramDateRangeSearch.search();

    assertThat(actualRequisitions, is(requisitions));
    verify(processingScheduleService).getUsedPeriodsForDateRange(facility, program, dateRangeStart, dateRangeEnd);
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
