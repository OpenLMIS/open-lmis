package org.openlmis.core.service;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramSupportedBuilder.*;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.openlmis.core.service.FacilityService.SUPPORTED_PROGRAMS_INVALID;

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

  private FacilityService facilityService;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    facilityService = new FacilityService(facilityRepository, programSupportedRepository, programRepository, supervisoryNodeService, requisitionGroupService);
  }

  @Test
  public void shouldReturnEmptyListIfUserIsNotAssignedAFacility() {
    when(facilityRepository.getHomeFacility(1)).thenReturn(null);
    assertTrue(facilityService.getAllForUser(1).isEmpty());
  }

  @Test
  public void shouldGetFacilityById() throws Exception {
    Integer facilityId = 1;
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
    facilityService.updateDataReportableAndActiveFor(facility);
    verify(facilityRepository).updateDataReportableAndActiveFor(facility);
  }

  @Test
  public void shouldNotGiveErrorIfSupportedProgramWithActiveFalseAndDateNotProvided() throws Exception {
    ProgramSupported programSupported = createSupportedProgram("facility code", "program code", false, null);

    int facilityId = 222;
    int programId = 111;
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
    expectedEx.expectMessage(SUPPORTED_PROGRAMS_INVALID);

    facilityService.uploadSupportedProgram(program);
  }

  @Test
  public void shouldNotGiveErrorIfProgramSupportedIsActiveAndDateProvided() throws Exception {
    String facilityCode = "some facility";
    String programCode = "some program";
    Date startDate = new Date();
    ProgramSupported program = createSupportedProgram(facilityCode, programCode, true, startDate);
    int facilityId = 222;
    int programId = 111;
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
    Integer userId = 1;
    Integer programId = 1;
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

    PowerMockito.when(facilityRepository.getIdForCode("invalid Code")).thenThrow(new DataException("Invalid Facility Code"));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Facility Code");

    facilityService.uploadSupportedProgram(programSupported);
  }

  @Test
  public void shouldInsertFacility() throws Exception {
    Facility facility = new Facility();

    facilityService.insert(facility);

    verify(facilityRepository).save(facility);
    verify(programSupportedRepository).addSupportedProgramsFor(facility);
  }

  @Test
  public void shouldThrowExceptionIfProgramsSupportedInvalidWhileInserting() throws Exception {
    Facility facility = new Facility();
    final Date nullDate = null;
    List<ProgramSupported> programs = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported)));
      add(make(a(defaultProgramSupported, with(supportedProgram, new Program(1,"HIV")), with(isActive, true), with(startDate, nullDate))));
    }};

    facility.setSupportedPrograms(programs);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(SUPPORTED_PROGRAMS_INVALID);

    facilityService.insert(facility);
  }

  @Test
  public void shouldThrowExceptionIfProgramsSupportedInvalidWhileUpdating() throws Exception {
    Facility facility = new Facility();
    final Date nullDate = null;
    List<ProgramSupported> programs = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported)));
      add(make(a(defaultProgramSupported, with(supportedProgram,new Program(1,"HIV")), with(isActive, true), with(startDate, nullDate))));
    }};

    facility.setSupportedPrograms(programs);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(SUPPORTED_PROGRAMS_INVALID);

    facilityService.update(facility);
  }

  @Test
  public void shouldUpdateFacility() throws Exception {
    Facility facility = new Facility();
    List<ProgramSupported> programsForFacility = new ArrayList<ProgramSupported>() {{
      add(make(a(defaultProgramSupported)));
      add(make(a(defaultProgramSupported, with(supportedProgram,new Program(2,"ARV")))));
    }};
    when(programSupportedRepository.getAllByFacilityId(facility.getId())).thenReturn(programsForFacility);

    facilityService.update(facility);

    verify(facilityRepository).save(facility);
    verify(programSupportedRepository).updateSupportedPrograms(facility, programsForFacility);
  }

  @Test
  public void shouldGetAllFacilitiesForUserAndRights() throws Exception {
    List<Facility> expectedFacilities = new ArrayList<>();
    when(facilityRepository.getForUserAndRights(1, Right.VIEW_REQUISITION, Right.APPROVE_REQUISITION)).thenReturn(expectedFacilities);
    List<Facility> actualFacilities = facilityService.getForUserAndRights(1, Right.VIEW_REQUISITION, Right.APPROVE_REQUISITION);

    assertThat(actualFacilities, is(expectedFacilities));
    verify(facilityRepository).getForUserAndRights(1, Right.VIEW_REQUISITION, Right.APPROVE_REQUISITION);
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
