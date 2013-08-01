/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;


import org.hamcrest.Matcher;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.FacilityFeedDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.db.categories.UnitTests;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({DateTime.class, FacilityService.class, FacilityServiceTest.class})
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
  public void shouldUpdateDataReportableAndActiveFor() throws Exception {
    Facility facility = make(a(defaultFacility));
    Facility parentFacility = new Facility(2l);
    parentFacility.setCode("PF");
    facility.setParentFacilityId(parentFacility.getId());
    FacilityFeedDTO facilityFeedDTO = new FacilityFeedDTO(facility, parentFacility);

    when(facilityRepository.updateDataReportableAndActiveFor(facility)).thenReturn(facility);

    when(facilityRepository.getById(facility.getId())).thenReturn(facility);
    when(facilityRepository.getById(facility.getParentFacilityId())).thenReturn(parentFacility);

    DateTime dateTime = new DateTime();
    mockStatic(DateTime.class);
    when(DateTime.now()).thenReturn(dateTime);
    UUID uuid = UUID.randomUUID();
    mockStatic(UUID.class);
    Mockito.when(UUID.randomUUID()).thenReturn(uuid);

    facilityService.updateDataReportableAndActiveFor(facility);

    verify(facilityRepository).updateDataReportableAndActiveFor(facility);
    verify(facilityRepository).getById(facility.getParentFacilityId());
    verify(eventService).notify(argThat(eventMatcher(uuid, "Facility", dateTime, "",
      facilityFeedDTO.getSerializedContents(), "facility")));

  }

  private static Matcher<Event> eventMatcher(final UUID uuid, final String title, final DateTime timestamp,
                                             final String uri, final String content, final String category) {
    return new ArgumentMatcher<Event>() {
      @Override
      public boolean matches(Object argument) {
        Event event = (Event) argument;
        return event.getUuid().equals(uuid.toString()) && event.getTitle().equals(title) && event.getTimeStamp().equals(timestamp) &&
          event.getUri().toString().equals(uri) && event.getContents().equals(content) && event.getCategory().equals(category);
      }
    };
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
  public void shouldSearchFacilitiesByCodeOrNameAndVirtualFacilityFlag() throws Exception {
    List<Facility> facilityList = Arrays.asList(new Facility());
    when(facilityRepository.searchFacilitiesByCodeOrNameAndVirtualFacilityFlag("query", true)).thenReturn(facilityList);

    List<Facility> returnedFacilities = facilityService.searchFacilitiesByCodeOrNameAndVirtualFacilityFlag("query", true);

    assertThat(returnedFacilities, is(facilityList));
  }

  @Test
  public void shouldSearchFacilitiesByCodeOrNameIfVirtualFacilityFlagIsNotPresenr() throws Exception {
    List<Facility> facilityList = Arrays.asList(new Facility());
    when(facilityRepository.searchFacilitiesByCodeOrName("query")).thenReturn(facilityList);

    List<Facility> returnedFacilities = facilityService.searchFacilitiesByCodeOrNameAndVirtualFacilityFlag("query", null);

    assertThat(returnedFacilities, is(facilityList));
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
  public void shouldUpdateFacilityAndNotifyForFeedIfCoreAttriButeChanges() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    List<ProgramSupported> programsForFacility = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported)));
      add(make(a(defaultProgramSupported, with(supportedProgram, new Program(2L, "ARV")))));
    }};
    when(programSupportedService.getAllByFacilityId(facility.getId())).thenReturn(programsForFacility);
    facility.setSupportedPrograms(programsForFacility);
    Facility savedFacility = make(a(FacilityBuilder.defaultFacility));
    savedFacility.setName("Updated Name");
    when(facilityRepository.getById(facility.getId())).thenReturn(savedFacility);

    facilityService.update(facility);

    verify(facilityRepository).save(facility);
    verify(programSupportedService).updateSupportedPrograms(facility);
    verify(eventService).notify(any(Event.class));
  }

  @Test
  public void shouldUpdateFacilityAndNotNotifyForFeedIfNoCoreAttributeChanges() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    List<ProgramSupported> programsForFacility = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported)));
      add(make(a(defaultProgramSupported, with(supportedProgram, new Program(2L, "ARV")))));
    }};
    facility.setSupportedPrograms(programsForFacility);
    Facility savedFacility = make(a(FacilityBuilder.defaultFacility));
    when(facilityRepository.getById(facility.getId())).thenReturn(savedFacility);

    facilityService.update(facility);

    verify(facilityRepository).save(facility);
    verify(programSupportedService).updateSupportedPrograms(facility);
    verify(eventService, never()).notify(any(Event.class));
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

  @Test
  public void shouldGetAllFacilitiesByModifiedDate() throws Exception {
    List<Facility> expectedFacilities = new ArrayList<>();
    Date dateModified = new Date();
    PowerMockito.when(facilityRepository.getAllByProgramSupportedModifiedDate(dateModified)).thenReturn(expectedFacilities);

    List<Facility> facilities = facilityService.getAllByProgramSupportedModifiedDate(dateModified);

    assertThat(facilities, is(expectedFacilities));
    verify(facilityRepository).getAllByProgramSupportedModifiedDate(dateModified);

  }

  @Test
  public void shouldGetFacilityWithReferenceDataForCode() throws Exception {

    String facilityCode = "F10";
    Long facilityId = 1l;
    Facility expectedFacility = new Facility();
    when(facilityRepository.getIdForCode(facilityCode)).thenReturn(facilityId);
    when(facilityRepository.getById(facilityId)).thenReturn(expectedFacility);
    Facility facility = facilityService.getFacilityWithReferenceDataForCode(facilityCode);

    verify(facilityRepository).getIdForCode(facilityCode);
    verify(facilityRepository).getById(facilityId);
    assertThat(facility, is(expectedFacility));
  }

}
