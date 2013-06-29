/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;


import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.FacilityFeedDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.openlmis.db.categories.UnitTests;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
public class FacilityServiceTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private FacilityRepository facilityRepository;
  @Mock
  private ProgramRepository programRepository;
  @Mock
  private ProgramSupportedRepository programSupportedRepository;
  @Mock
  private SupervisoryNodeService supervisoryNodeService;
  @Mock
  private RequisitionGroupService requisitionGroupService;

  @Mock
  private GeographicZoneRepository geographicZoneRepository;

  private FacilityService facilityService;

  @Mock
  private EventService eventService;

  @Mock
  private AllocationProgramProductService allocationProgramProductService;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    facilityService = new FacilityService(facilityRepository, programSupportedRepository, programRepository, supervisoryNodeService,
      requisitionGroupService, geographicZoneRepository, eventService, allocationProgramProductService);
  }

  @Test
  public void shouldReturnNullIfUserIsNotAssignedAFacility() {
    when(facilityRepository.getHomeFacility(1L)).thenReturn(null);
    assertThat(facilityService.getHomeFacility(1L), is(nullValue()));
  }

  @Test
  public void shouldGetFacilityById() throws Exception {
    Long facilityId = 1L;
    List<ProgramSupported> supportedPrograms = Arrays.asList(new ProgramSupported());
    Facility facility = new Facility();

    when(programSupportedRepository.getAllByFacilityId(facilityId)).thenReturn(supportedPrograms);
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
    when(programSupportedRepository.getAllByFacilityId(facility.getId())).thenReturn(programsSupported);
    when(facilityRepository.getById(facility.getId())).thenReturn(facility);

    Facility returnedFacility = facilityService.updateDataReportableAndActiveFor(facility);

    assertThat(returnedFacility, is(facility));
    assertThat(returnedFacility.getSupportedPrograms(), is(programsSupported));
    verify(facilityRepository).updateDataReportableAndActiveFor(facility);
    verify(programSupportedRepository).getAllByFacilityId(facility.getId());
    verify(facilityRepository).getById(facility.getId());
  }

  @Test
  public void shouldNotGiveErrorIfSupportedProgramWithActiveFalseAndDateNotProvided() throws Exception {
    ProgramSupported programSupported = createSupportedProgram("facility code", "program code", false, null);

    Long facilityId = 222L;
    Long programId = 111L;
    when(facilityRepository.getIdForCode("facility code")).thenReturn(facilityId);
    when(programRepository.getIdByCode("program code")).thenReturn(programId);

    facilityService.uploadSupportedProgram(programSupported);

    assertThat(programSupported.getFacilityId(), is(facilityId));
    assertThat(programSupported.getProgram().getId(), is(programId));
    assertThat(programSupported.getActive(), is(false));
    assertThat(programSupported.getStartDate(), is(nullValue()));

    verify(programSupportedRepository).addSupportedProgram(programSupported);
  }


  @Test
  public void shouldGiveErrorIfSupportedProgramWithActiveTrueAndStartDateNotProvided() throws Exception {
    ProgramSupported program = createSupportedProgram("facility code", "program code", true, null);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("supported.programs.invalid");

    facilityService.uploadSupportedProgram(program);
  }

  @Test
  public void shouldNotGiveErrorIfProgramSupportedIsActiveAndDateProvided() throws Exception {
    String facilityCode = "some facility";
    String programCode = "some program";
    Date startDate = new Date();
    ProgramSupported program = createSupportedProgram(facilityCode, programCode, true, startDate);
    Long facilityId = 222L;
    Long programId = 111L;
    when(facilityRepository.getIdForCode(facilityCode)).thenReturn(facilityId);
    when(programRepository.getIdByCode(programCode)).thenReturn(programId);

    facilityService.uploadSupportedProgram(program);

    assertThat(program.getFacilityId(), is(facilityId));
    assertThat(program.getProgram().getId(), is(programId));
    assertThat(program.getActive(), is(true));
    assertThat(program.getStartDate(), is(startDate));

    verify(programSupportedRepository).addSupportedProgram(program);
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
    List<Facility> facilities = Arrays.asList(new Facility());
    when(facilityRepository.searchFacilitiesByCodeOrName("searchParam")).thenReturn(facilities);

    List<Facility> returnedFacilities = facilityService.searchFacilitiesByCodeOrName("searchParam");

    assertThat(returnedFacilities, is(facilities));
  }

  @Test
  public void shouldRaiseErrorWhenFacilityWithGivenCodeDoesNotExistWhileSavingProgramSupported() throws Exception {
    ProgramSupported programSupported = createSupportedProgram("invalid Code", "valid Code", true, new Date());

    PowerMockito.when(facilityRepository.getIdForCode("invalid Code")).thenThrow(new DataException("error.facility.code.invalid"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.facility.code.invalid");

    facilityService.uploadSupportedProgram(programSupported);
  }

  @Test
  public void shouldInsertFacility() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));

    whenNew(FacilityFeedDTO.class).withArguments(facility).thenReturn(new FacilityFeedDTO(facility));
    when(facilityRepository.getById(facility.getId())).thenReturn(facility);
    facilityService.insert(facility);

    verify(facilityRepository).save(facility);
    verify(programSupportedRepository).addSupportedProgramsFor(facility);
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
    when(programSupportedRepository.getAllByFacilityId(facility.getId())).thenReturn(programsForFacility);
    whenNew(FacilityFeedDTO.class).withArguments(facility).thenReturn(new FacilityFeedDTO(facility));
    when(facilityRepository.getById(facility.getId())).thenReturn(facility);

    facilityService.update(facility);

    verify(facilityRepository).save(facility);
    verify(programSupportedRepository).updateSupportedPrograms(facility, programsForFacility);
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
  public void shouldInsertProgramSupportedIfDoesNotExist() {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("F1");
    Program program = new Program();
    program.setCode("P1");
    programSupported.setProgram(program);
    programSupported.setModifiedDate(new Date());
    when(facilityRepository.getIdForCode("F1")).thenReturn(1L);
    when(programRepository.getIdByCode("P1")).thenReturn(1L);
    when(programSupportedRepository.getByFacilityIdAndProgramId(1L, 1L)).thenReturn(null);

    facilityService.uploadSupportedProgram(programSupported);

    verify(programSupportedRepository).addSupportedProgram(programSupported);
  }

  @Test
  public void shouldUpdateProgramSupportedIfItExists() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("F1");
    Program program = new Program();
    program.setCode("P1");
    programSupported.setProgram(program);
    programSupported.setId(1L);
    when(facilityRepository.getIdForCode("F1")).thenReturn(1L);
    when(programRepository.getIdByCode("P1")).thenReturn(2L);

    facilityService.uploadSupportedProgram(programSupported);

    assertThat(programSupported.getFacilityId(), is(1L));
    assertThat(programSupported.getProgram().getId(), is(2L));
    verify(programSupportedRepository).updateSupportedProgram(programSupported);
  }


  @Test
  public void shouldGetAllFacilitiesInDeliveryZoneForSupportedProgram() throws Exception {
    List<Facility> memberFacilities = new ArrayList<>();
    Facility facility = new Facility();
    facility.setId(1L);
    memberFacilities.add(facility);

    Long deliveryZoneId = 1l;
    Long programId = 1l;
    when(facilityRepository.getAllInDeliveryZoneFor(deliveryZoneId, programId)).thenReturn(memberFacilities);
    ProgramSupported programSupported = new ProgramSupported();
    List<ProgramSupported> programsSupported = new ArrayList<>();
    programsSupported.add(programSupported);
    when(programSupportedRepository.getByFacilityIdAndProgramId(facility.getId(), programId)).thenReturn(programSupported);
    List<AllocationProgramProduct> allocationProgramProduct = new ArrayList<>();
    when(allocationProgramProductService.getByFacilityAndProgram(facility.getId(), programId)).thenReturn(allocationProgramProduct);

    List<Facility> facilities = facilityService.getAllForDeliveryZoneAndProgram(deliveryZoneId, programId);

    assertThat(facilities, is(memberFacilities));
    assertThat(facilities.get(0).getSupportedPrograms(), is(programsSupported));
    assertThat(facilities.get(0).getSupportedPrograms().get(0).getProgramProducts(), is(allocationProgramProduct));
    verify(programSupportedRepository).getByFacilityIdAndProgramId(facility.getId(), programId);
    verify(allocationProgramProductService).getByFacilityAndProgram(facility.getId(), programId);
    verify(facilityRepository).getAllInDeliveryZoneFor(deliveryZoneId, programId);
  }

  private ProgramSupported createSupportedProgram(String facilityCode, String programCode, boolean active, Date startDate) {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode(facilityCode);
    Program program = new Program();
    program.setCode(programCode);
    programSupported.setProgram(program);
    programSupported.setActive(active);
    programSupported.setStartDate(startDate);
    return programSupported;
  }
}
