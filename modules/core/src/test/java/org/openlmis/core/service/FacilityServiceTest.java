package org.openlmis.core.service;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.*;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

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

  private FacilityService service;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    service = new FacilityService(facilityRepository, programSupportedRepository, programRepository, supervisoryNodeService, requisitionGroupService);
  }

  @Test
  public void shouldStoreFacility() throws Exception {
    Facility facility = make(a(defaultFacility));
    service.save(facility);
    verify(facilityRepository).save(facility);
  }

  @Test
  public void shouldReturnEmptyListIfUserIsNotAssignedAFacility() {
    when(facilityRepository.getHomeFacility(1)).thenReturn(null);
    assertTrue(service.getAllForUser(1).isEmpty());
  }

  @Test
  public void shouldGetFacilityById() throws Exception {
    Integer facilityId = 1;
    List<Program> supportedPrograms = Arrays.asList(new Program());
    Facility facility = new Facility();

    when(programRepository.getByFacility(facilityId)).thenReturn(supportedPrograms);
    when(facilityRepository.getById(facilityId)).thenReturn(facility);

    Facility returnedFacility = service.getById(facilityId);

    assertThat(returnedFacility, is(facility));
    assertThat(returnedFacility.getSupportedPrograms(), is(supportedPrograms));
  }

  @Test
  public void shouldUpdateDataReportableAndActiveFor() {
    Facility facility = make(a(defaultFacility));
    service.updateDataReportableAndActiveFor(facility);
    verify(facilityRepository).updateDataReportableAndActiveFor(facility);
  }

  @Test
  public void shouldAddSupportedProgram() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("facility code");
    programSupported.setProgramCode("program code");

    int facilityId = 222;
    int programId = 111;
    when(facilityRepository.getIdForCode("facility code")).thenReturn(facilityId);
    when(programRepository.getIdByCode("program code")).thenReturn(programId);

    service.uploadSupportedProgram(programSupported);

    assertThat(programSupported.getModifiedDate(), is(notNullValue()));
    assertThat(programSupported.getFacilityId(), is(facilityId));
    assertThat(programSupported.getProgramId(), is(programId));

    verify(programSupportedRepository).addSupportedProgram(programSupported);
  }

  @Test
  public void shouldReturnUserSupervisedFacilitiesForAProgram() {
    Integer userId = 1;
    Integer programId = 1;
    List<Facility> facilities = new ArrayList<>();
    List<SupervisoryNode> supervisoryNodes = new ArrayList<>();
    List<RequisitionGroup> requisitionGroups = new ArrayList<>();
    when(facilityRepository.getFacilitiesBy(programId, requisitionGroups)).thenReturn(facilities);
    when(supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION)).thenReturn(supervisoryNodes);
    when(requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes)).thenReturn(requisitionGroups);

    List<Facility> result = service.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION);

    verify(facilityRepository).getFacilitiesBy(programId, requisitionGroups);
    verify(supervisoryNodeService).getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION);
    verify(requisitionGroupService).getRequisitionGroupsBy(supervisoryNodes);
    assertThat(result, is(facilities));
  }

  @Test
  public void shouldSearchFacilitiesByCodeOrName() throws Exception {
    List<Facility> facilities = Arrays.asList(new Facility());
    when(facilityRepository.searchFacilitiesByCodeOrName("searchParam")).thenReturn(facilities);

    List<Facility> returnedFacilities = service.searchFacilitiesByCodeOrName("searchParam");

    assertThat(returnedFacilities, is(facilities));
  }

  @Test
  public void shouldRaiseErrorWhenFacilityWithGivenCodeDoesNotExistWhileSavingProgramSupported() throws Exception {
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setFacilityCode("invalid Code");
    programSupported.setProgramCode("valid Code");

    PowerMockito.when(facilityRepository.getIdForCode("invalid Code")).thenThrow(new DataException("Invalid Facility Code"));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Facility Code");

    service.uploadSupportedProgram(programSupported);
  }

  @Test
  public void shouldAddProgramsSupportedByAFacility() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(null);
    List<Program> programs = new ArrayList<Program>() {{
      add(make(a(defaultProgram)));
      add(make(a(defaultProgram)));
    }};
    facility.setSupportedPrograms(programs);

    service.save(facility);

    verify(facilityRepository).save(facility);
    verify(programSupportedRepository).addSupportedProgramsFor(facility);
  }

  @Test
  public void shouldUpdateSupportedProgramsForFacilityIfIdIsDefined() throws Exception {
    Facility facility = make(a(defaultFacility));
    facility.setId(1);

    List<Program> programs = new ArrayList<Program>() {{
      add(make(a(ProgramBuilder.defaultProgram)));
      add(make(a(ProgramBuilder.defaultProgram, with(programCode, "HIV"), with(programId, 1))));
    }};

    facility.setSupportedPrograms(programs);

    List<Program> programsForFacility = new ArrayList<Program>() {{
      add(make(a(ProgramBuilder.defaultProgram)));
      add(make(a(ProgramBuilder.defaultProgram, with(programCode, "ARV"), with(programId, 2))));
    }};

    when(programRepository.getByFacility(facility.getId())).thenReturn(programsForFacility);

    service.save(facility);

    verify(programRepository).getByFacility(facility.getId());
    verify(programSupportedRepository).updateSupportedPrograms(facility, programsForFacility);
  }
}
