package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupMemberMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupProgramScheduleMapper;

import java.util.ArrayList;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_CODE;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.PROGRAM_CODE;
import static org.openlmis.core.builder.ProgramBuilder.PROGRAM_ID;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;

@RunWith(MockitoJUnitRunner.class)
public class RequisitionGroupMemberRepositoryTest {

  ArrayList<Integer> programIdList;
  public static final Integer RG_ID = 1;
  public static final Integer FACILITY_ID = 100;
  RequisitionGroup requisitionGroup;
  RequisitionGroupMember requisitionGroupMember;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  RequisitionGroupMemberMapper requisitionGroupMemberMapper;

  @Mock
  RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;

  @Mock
  RequisitionGroupMapper requisitionGroupMapper;

  @Mock
  FacilityRepository facilityRepository;

  @Mock
  ProgramMapper programMapper;
  private RequisitionGroupMemberRepository repository;


  @Before
  public void setUp() throws Exception {
    requisitionGroup = make(a(defaultRequisitionGroup));
    Facility facility = make(a(defaultFacility));

    requisitionGroupMember = new RequisitionGroupMember();
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMember.setFacility(facility);

    programIdList = new ArrayList<>();
    programIdList.add(1);

    initMocks(this);
    repository = new RequisitionGroupMemberRepository(requisitionGroupMemberMapper, requisitionGroupProgramScheduleMapper, requisitionGroupMapper, facilityRepository, programMapper);
  }


  @Test
  public void shouldGiveErrorIfRGDoesNotExist() throws Exception {
    when(requisitionGroupMapper.getIdForCode(requisitionGroupMember.getRequisitionGroup().getCode())).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Requisition Group does not exist");

    repository.insert(requisitionGroupMember);
  }

  @Test
  public void shouldGiveErrorIfFacilityDoesNotExist() throws Exception {
    when(requisitionGroupMapper.getIdForCode(requisitionGroupMember.getRequisitionGroup().getCode())).thenReturn(RG_ID);
    when(facilityRepository.getIdForCode(requisitionGroupMember.getFacility().getCode())).thenThrow(new DataException("Invalid Facility Code"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Facility Code");

    repository.insert(requisitionGroupMember);
  }

  @Test
  public void shouldGiveErrorIfNoProgramsMappedToRG() throws Exception {
    when(requisitionGroupMapper.getIdForCode(requisitionGroupMember.getRequisitionGroup().getCode())).thenReturn(RG_ID);
    when(facilityRepository.getIdForCode(requisitionGroupMember.getFacility().getCode())).thenReturn(FACILITY_ID);

    when(requisitionGroupProgramScheduleMapper.getProgramIDsById(RG_ID)).thenReturn(new ArrayList<Integer>());

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("No Program(s) mapped for Requisition Group");

    repository.insert(requisitionGroupMember);
  }

  @Test
  public void shouldGiveErrorIfFacilityIsBeingMappedToAProgramWhichItIsAlreadyMappedTo() throws Exception {
    when(requisitionGroupMapper.getIdForCode(requisitionGroupMember.getRequisitionGroup().getCode())).thenReturn(RG_ID);
    when(facilityRepository.getIdForCode(requisitionGroupMember.getFacility().getCode())).thenReturn(FACILITY_ID);

    ArrayList<Integer> programIdsForRequisitionGroup = new ArrayList<>();
    Integer commonProgramId = 1;
    programIdsForRequisitionGroup.add(commonProgramId);
    programIdsForRequisitionGroup.add(2);
    programIdsForRequisitionGroup.add(3);

    when(requisitionGroupProgramScheduleMapper.getProgramIDsById(RG_ID)).thenReturn(programIdsForRequisitionGroup);

    ArrayList<Integer> requisitionGroupProgramIdsForFacility = new ArrayList<>();
    requisitionGroupProgramIdsForFacility.add(commonProgramId);
    requisitionGroupProgramIdsForFacility.add(4);

    when(requisitionGroupMemberMapper.getRequisitionGroupProgramIdsForId(FACILITY_ID)).thenReturn(requisitionGroupProgramIdsForFacility);

    when(programMapper.getById(commonProgramId)).thenReturn(make(a(defaultProgram)));

    when(requisitionGroupMemberMapper.getRGCodeForProgramAndFacility(commonProgramId, FACILITY_ID)).thenReturn("DCODE");

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Facility " + FACILITY_CODE + " is already assigned to Requisition Group DCODE running same program " + PROGRAM_CODE);

    repository.insert(requisitionGroupMember);
  }

  @Test
  public void shouldGiveErrorIfDuplicateMappingFound() throws Exception {
    when(requisitionGroupMapper.getIdForCode(requisitionGroupMember.getRequisitionGroup().getCode())).thenReturn(RG_ID);
    when(facilityRepository.getIdForCode(requisitionGroupMember.getFacility().getCode())).thenReturn(FACILITY_ID);
    when(requisitionGroupProgramScheduleMapper.getProgramIDsById(RG_ID)).thenReturn(programIdList);

    when(requisitionGroupMemberMapper.doesMappingExist(RG_ID, FACILITY_ID)).thenReturn(1);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Facility to Requisition Group mapping already exists");

    repository.insert(requisitionGroupMember);
  }

  @Test
  public void shouldSaveMappingIfAllConditionsCorrectlyMet() throws Exception {
    when(requisitionGroupMapper.getIdForCode(requisitionGroupMember.getRequisitionGroup().getCode())).thenReturn(RG_ID);
    when(facilityRepository.getIdForCode(requisitionGroupMember.getFacility().getCode())).thenReturn(FACILITY_ID);
    when(requisitionGroupProgramScheduleMapper.getProgramIDsById(RG_ID)).thenReturn(programIdList);

    repository.insert(requisitionGroupMember);

    verify(requisitionGroupMemberMapper).insert(requisitionGroupMember);
    assertThat(requisitionGroupMember.getFacility().getId(), is(notNullValue()));
    assertThat(requisitionGroupMember.getRequisitionGroup().getId(), is(notNullValue()));

  }

  @Test
  public void shouldReturnRGCodeForFacilityProgram() {
    String expectedRGCode = "test rg code";
    when(requisitionGroupMemberMapper.getRGCodeForProgramAndFacility(FACILITY_ID, PROGRAM_ID)).thenReturn(expectedRGCode);
    String rgCode = repository.getRGCodeForProgramAndFacility(FACILITY_ID, PROGRAM_ID);
    assertThat(rgCode, is(expectedRGCode));
  }

}

