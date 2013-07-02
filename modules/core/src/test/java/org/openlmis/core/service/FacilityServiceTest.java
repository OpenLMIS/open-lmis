/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;


import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.FacilityFeedDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityServiceTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private FacilityRepository facilityRepository;
  @Mock
  private ProgramRepository programRepository;
  @Mock
  private ProgramSupportedService programSupportedService;
  @Mock
  private SupervisoryNodeService supervisoryNodeService;
  @Mock
  private RequisitionGroupService requisitionGroupService;

  @Mock
  private GeographicZoneRepository geographicZoneRepository;

  @Mock
  private EventService eventService;

  @Mock
  private FacilityProgramProductService facilityProgramProductService;

  @InjectMocks
  private FacilityService facilityService;

  @Test
  public void shouldReturnNullIfUserIsNotAssignedAFacility() {
    when(facilityRepository.getHomeFacility(1L)).thenReturn(null);
    assertThat(facilityService.getHomeFacility(1L), is(nullValue()));
  }

  @Test
  public void shouldGetFacilityById() throws Exception {
    Long facilityId = 1L;
    List<ProgramSupported> supportedPrograms = asList(new ProgramSupported());
    Facility facility = new Facility();

    when(programSupportedService.getAllByFacilityId(facilityId)).thenReturn(supportedPrograms);
    when(facilityRepository.getById(facilityId)).thenReturn(facility);

    Facility returnedFacility = facilityService.getById(facilityId);

    assertThat(returnedFacility, is(facility));
    assertThat(returnedFacility.getSupportedPrograms(), is(supportedPrograms));
  }

  @Test
  public void shouldUpdateDataReportableAndActiveFor() {
    Facility facility = make(a(defaultFacility));
    when(facilityRepository.updateDataReportableAndActiveFor(facility)).thenReturn(facility);
    List<ProgramSupported> programsSupported = new ArrayList<ProgramSupported>() {{
      add(new ProgramSupported());
    }};
    when(programSupportedService.getAllByFacilityId(facility.getId())).thenReturn(programsSupported);
    when(facilityRepository.getById(facility.getId())).thenReturn(facility);

    Facility returnedFacility = facilityService.updateDataReportableAndActiveFor(facility);

    assertThat(returnedFacility, is(facility));
    assertThat(returnedFacility.getSupportedPrograms(), is(programsSupported));
    verify(facilityRepository).updateDataReportableAndActiveFor(facility);
    verify(programSupportedService).getAllByFacilityId(facility.getId());
    verify(facilityRepository).getById(facility.getId());
  }

  @Test
  public void shouldReturnUserSupervisedFacilitiesForAProgram() {
    Long userId = 1L;
    Long programId = 1L;
    List<Facility> facilities = new ArrayList<>();
    List<SupervisoryNode> supervisoryNodes = new ArrayList<>();
    List<RequisitionGroup> requisitionGroups = new ArrayList<>();
    when(facilityRepository.getFacilitiesBy(programId, requisitionGroups)).thenReturn(facilities);
    when(supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION)).thenReturn(supervisoryNodes);
    when(requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes)).thenReturn(requisitionGroups);

    List<Facility> result = facilityService.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION);

    verify(facilityRepository).getFacilitiesBy(programId, requisitionGroups);
    verify(supervisoryNodeService).getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION);
    verify(requisitionGroupService).getRequisitionGroupsBy(supervisoryNodes);
    assertThat(result, is(facilities));
  }

  @Test
  public void shouldSearchFacilitiesByCodeOrName() throws Exception {
    List<Facility> facilities = asList(new Facility());
    when(facilityRepository.searchFacilitiesByCodeOrName("searchParam")).thenReturn(facilities);

    List<Facility> returnedFacilities = facilityService.searchFacilitiesByCodeOrName("searchParam");

    assertThat(returnedFacilities, is(facilities));
  }


  @Test
  public void shouldInsertFacility() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));

    whenNew(FacilityFeedDTO.class).withArguments(facility).thenReturn(new FacilityFeedDTO(facility));
    when(facilityRepository.getById(facility.getId())).thenReturn(facility);
    facilityService.insert(facility);

    verify(facilityRepository).save(facility);
    verify(programSupportedService).addSupportedProgramsFor(facility);
    verify(facilityRepository).getById(facility.getId());
    verify(eventService).notify(any(Event.class));
  }

  @Test
  public void shouldThrowExceptionIfProgramsSupportedInvalidWhileInserting() throws Exception {
    Facility facility = new Facility();
    final Date nullDate = null;
    List<ProgramSupported> programs = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported)));
      add(make(a(defaultProgramSupported, with(supportedProgram, new Program(1L, "HIV")), with(isActive, true), with(startDate, nullDate))));
    }};

    facility.setSupportedPrograms(programs);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("supported.programs.invalid");

    facilityService.insert(facility);
  }

  @Test
  public void shouldThrowExceptionIfProgramsSupportedInvalidWhileUpdating() throws Exception {
    Facility facility = new Facility();
    final Date nullDate = null;
    List<ProgramSupported> programs = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported)));
      add(make(a(defaultProgramSupported, with(supportedProgram, new Program(1L, "HIV")), with(isActive, true), with(startDate, nullDate))));
    }};

    facility.setSupportedPrograms(programs);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("supported.programs.invalid");

    facilityService.update(facility);
  }

  @Test
  public void shouldUpdateFacility() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    List<ProgramSupported> programsForFacility = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported)));
      add(make(a(defaultProgramSupported, with(supportedProgram, new Program(2L, "ARV")))));
    }};
    when(programSupportedService.getAllByFacilityId(facility.getId())).thenReturn(programsForFacility);
    whenNew(FacilityFeedDTO.class).withArguments(facility).thenReturn(new FacilityFeedDTO(facility));
    when(facilityRepository.getById(facility.getId())).thenReturn(facility);

    facilityService.update(facility);

    verify(facilityRepository).save(facility);
    verify(programSupportedService).updateSupportedPrograms(facility, programsForFacility);
    verify(eventService).notify(any(Event.class));
  }

  @Test
  public void shouldGetAllFacilitiesForUserAndRights() throws Exception {
    //Arrange
    Right[] rights = {Right.VIEW_REQUISITION, Right.APPROVE_REQUISITION};
    Facility homeFacility = new Facility();
    List<Facility> supervisedFacilities = new ArrayList<>();
    supervisedFacilities.add(homeFacility);
    List<SupervisoryNode> supervisoryNodes = new ArrayList<>();
    List<RequisitionGroup> requisitionGroups = new ArrayList<>();
    when(facilityRepository.getHomeFacilityForRights(1L, rights)).thenReturn(homeFacility);
    when(supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(1L, rights)).thenReturn(supervisoryNodes);
    when(requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes)).thenReturn(requisitionGroups);
    when(facilityRepository.getAllInRequisitionGroups(requisitionGroups)).thenReturn(supervisedFacilities);

    //Act
    List<Facility> actualFacilities = facilityService.getForUserAndRights(1L, rights);

    //Assert
    assertThat(actualFacilities, is(supervisedFacilities));
    assertThat(actualFacilities.contains(homeFacility), is(true));
    verify(facilityRepository).getHomeFacilityForRights(1L, rights);
    verify(supervisoryNodeService).getAllSupervisoryNodesInHierarchyBy(1L, rights);
    verify(requisitionGroupService).getRequisitionGroupsBy(supervisoryNodes);
    verify(facilityRepository).getAllInRequisitionGroups(requisitionGroups);
  }

  @Test
  public void shouldNotGetHomeFacilityWhenItIsNull() throws Exception {
    //Arrange
    Right[] rights = {Right.VIEW_REQUISITION, Right.APPROVE_REQUISITION};
    Facility supervisedFacility = new Facility();
    List<Facility> supervisedFacilities = new ArrayList<>();
    supervisedFacilities.add(supervisedFacility);
    List<SupervisoryNode> supervisoryNodes = new ArrayList<>();
    List<RequisitionGroup> requisitionGroups = new ArrayList<>();
    when(facilityRepository.getHomeFacilityForRights(1L, rights)).thenReturn(null);
    when(supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(1L, rights)).thenReturn(supervisoryNodes);
    when(requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes)).thenReturn(requisitionGroups);
    when(facilityRepository.getAllInRequisitionGroups(requisitionGroups)).thenReturn(supervisedFacilities);

    //Act
    List<Facility> actualFacilities = facilityService.getForUserAndRights(1L, rights);

    //Assert
    assertThat(actualFacilities, is(supervisedFacilities));
    assertThat(actualFacilities.size(), is(1));
    verify(facilityRepository).getHomeFacilityForRights(1L, rights);
    verify(supervisoryNodeService).getAllSupervisoryNodesInHierarchyBy(1L, rights);
    verify(requisitionGroupService).getRequisitionGroupsBy(supervisoryNodes);
    verify(facilityRepository).getAllInRequisitionGroups(requisitionGroups);
  }


  @Test
  public void shouldGetAllFacilitiesInDeliveryZoneForSupportedProgram() throws Exception {
    List<Facility> memberFacilities = new ArrayList<>();
    Facility facility = new Facility(1L);

    Facility facility2 = new Facility(2L);

    memberFacilities.add(facility);
    memberFacilities.add(facility2);

    Long deliveryZoneId = 1l;
    Long programId = 1l;
    when(facilityRepository.getAllInDeliveryZoneFor(deliveryZoneId, programId)).thenReturn(memberFacilities);
    ProgramSupported programSupported = new ProgramSupported();
    when(programSupportedService.getFilledByFacilityIdAndProgramId(facility.getId(), programId)).thenReturn(programSupported);
    when(programSupportedService.getFilledByFacilityIdAndProgramId(facility2.getId(), programId)).thenReturn(programSupported);

    List<Facility> facilities = facilityService.getAllForDeliveryZoneAndProgram(deliveryZoneId, programId);

    assertThat(facilities, is(memberFacilities));
    assertThat(facilities.get(0).getSupportedPrograms(), is(asList(programSupported)));
    assertThat(facilities.get(1).getSupportedPrograms(), is(asList(programSupported)));
    verify(facilityRepository).getAllInDeliveryZoneFor(deliveryZoneId, programId);
  }
}
