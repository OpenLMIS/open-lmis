/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mock;
import org.openlmis.core.builder.RequisitionGroupBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.*;
import org.openlmis.db.categories.UnitTests;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_CODE;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.PROGRAM_CODE;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@Category(UnitTests.class)
@PrepareForTest(RequisitionGroupMemberService.class)
public class RequisitionGroupMemberServiceTest {

  RequisitionGroupMemberService service;

  public static final Long RG_ID = 1L;
  public static final Long FACILITY_ID = 100L;

  @Mock
  RequisitionGroupMemberRepository requisitionGroupMemberRepository;
  @Mock
  FacilityRepository facilityRepository;
  @Mock
  RequisitionGroupRepository requisitionGroupRepository;
  @Mock
  RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository;
  @Mock
  ProgramRepository programRepository;

  RequisitionGroup requisitionGroup;
  RequisitionGroupMember requisitionGroupMember;
  List<Long> programIdList;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    requisitionGroup = make(a(defaultRequisitionGroup));
    requisitionGroup.setId(RG_ID);
    Facility facility = make(a(defaultFacility));
    facility.setId(FACILITY_ID);

    requisitionGroupMember = new RequisitionGroupMember();
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMember.setFacility(facility);

    programIdList = new ArrayList<>();
    programIdList.add(1L);

    service = new RequisitionGroupMemberService(requisitionGroupMemberRepository, facilityRepository,
      requisitionGroupRepository, requisitionGroupProgramScheduleRepository, programRepository);
  }

  @Test
  public void shouldSaveRGMember() throws Exception {
    when(requisitionGroupRepository.getByCode(requisitionGroupMember.getRequisitionGroup())).thenReturn(requisitionGroup);
    when(requisitionGroupProgramScheduleRepository.getProgramIDsForRequisitionGroup(requisitionGroupMember.getRequisitionGroup().getId())).thenReturn(programIdList);

    service.save(requisitionGroupMember);

    verify(requisitionGroupMemberRepository).insert(requisitionGroupMember);
  }

  @Test
  public void shouldGiveErrorIfRGDoesNotExist() throws Exception {
    when(requisitionGroupRepository.getByCode(
      requisitionGroupMember.getRequisitionGroup())).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.requisition.group.not.exist"));

    service.save(requisitionGroupMember);
  }

  @Test
  public void shouldGiveErrorIfFacilityDoesNotExist() throws Exception {
    when(requisitionGroupRepository.getByCode(requisitionGroupMember.getRequisitionGroup())).thenReturn(requisitionGroup);
    when(facilityRepository.getIdForCode(requisitionGroupMember.getFacility().getCode())).thenThrow(new DataException("error.facility.code.invalid"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.facility.code.invalid");

    service.save(requisitionGroupMember);
  }

  @Test
  public void shouldGiveErrorIfNoProgramsMappedToRG() throws Exception {
    when(requisitionGroupRepository.getByCode(requisitionGroupMember.getRequisitionGroup())).thenReturn(requisitionGroup);
    when(facilityRepository.getIdForCode(requisitionGroupMember.getFacility().getCode())).thenReturn(FACILITY_ID);

    when(requisitionGroupProgramScheduleRepository.getProgramIDsForRequisitionGroup(RG_ID)).thenReturn(new ArrayList<Long>());

    expectedEx.expect(DataException.class);
    expectedEx.expect(dataExceptionMatcher("error.no.program.mapped.requisition.group"));

    service.save(requisitionGroupMember);
  }

  @Test
  public void shouldGiveErrorIfFacilityIsBeingMappedToAProgramWhichItIsAlreadyMappedTo() throws Exception {
    when(requisitionGroupRepository.getByCode(requisitionGroupMember.getRequisitionGroup())).thenReturn(requisitionGroup);
    when(facilityRepository.getIdForCode(requisitionGroupMember.getFacility().getCode())).thenReturn(FACILITY_ID);

    ArrayList<Long> programIdsForRequisitionGroup = new ArrayList<>();
    Long commonProgramId = 1L;
    programIdsForRequisitionGroup.add(commonProgramId);
    programIdsForRequisitionGroup.add(2L);
    programIdsForRequisitionGroup.add(3L);

    when(requisitionGroupProgramScheduleRepository.getProgramIDsForRequisitionGroup(RG_ID)).thenReturn(programIdsForRequisitionGroup);

    ArrayList<Long> requisitionGroupProgramIdsForFacility = new ArrayList<>();
    requisitionGroupProgramIdsForFacility.add(commonProgramId);
    requisitionGroupProgramIdsForFacility.add(4L);

    when(requisitionGroupMemberRepository.getRequisitionGroupProgramIdsForFacilityId(FACILITY_ID)).thenReturn(requisitionGroupProgramIdsForFacility);

    Program commonProgram = make(a(defaultProgram));
    when(programRepository.getById(commonProgramId)).thenReturn(commonProgram);

    RequisitionGroup rg = make(a(defaultRequisitionGroup, with(RequisitionGroupBuilder.code, "DCODE")));
    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(commonProgram, requisitionGroupMember.getFacility())).thenReturn(rg);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Facility " + FACILITY_CODE + " is already assigned to Requisition Group DCODE running same program " + PROGRAM_CODE);

    service.save(requisitionGroupMember);
  }

  @Test
  public void shouldNotInsertIfRequisitionGroupMemberIdIsPresent() throws Exception {

    when(requisitionGroupRepository.getByCode(requisitionGroupMember.getRequisitionGroup())).thenReturn(requisitionGroup);

    requisitionGroupMember.setId(1L);

    service.save(requisitionGroupMember);

    verify(requisitionGroupMemberRepository, never()).insert(requisitionGroupMember);
  }

  @Test
  public void shouldGetAllRequisitionGroupMembersByFacilityId() throws Exception {
    Long facilityId = 4L;
    List<RequisitionGroupMember> expectedMembers = asList(new RequisitionGroupMember());
    when(requisitionGroupMemberRepository.getAllRequisitionGroupMembersByFacility(facilityId)).thenReturn(expectedMembers);

    List<RequisitionGroupMember> actualMembers = service.getAllRequisitionGroupMembersByFacility(facilityId);

    verify(requisitionGroupMemberRepository).getAllRequisitionGroupMembersByFacility(facilityId);
    assertThat(actualMembers, is(expectedMembers));
  }

  @Test
  public void shouldInsertRequisitionGroupMembers() throws Exception {

    RequisitionGroupMember member = new RequisitionGroupMember();
    Facility facility = new Facility();
    facility.setId(1L);
    member.setFacility(facility);
    member.setId(1L);
    member.getFacility().setCode("code");

    RequisitionGroupMemberService serviceSpy = PowerMockito.spy(service);
    PowerMockito.doNothing().when(serviceSpy,
      method(RequisitionGroupMemberService.class, "validateIfFacilityIsAlreadyAssignedToRequisitionGroupForProgram", RequisitionGroupMember.class)).withArguments(member);

    serviceSpy.insert(member);

    verify(facilityRepository).getIdForCode(member.getFacility().getCode());
    verify(requisitionGroupMemberRepository).insert(member);
  }
}
