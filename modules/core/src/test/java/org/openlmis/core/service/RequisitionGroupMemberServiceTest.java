/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.RequisitionGroupBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.*;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_CODE;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.PROGRAM_CODE;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
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

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Requisition Group does not exist");

    service.save(requisitionGroupMember);
  }

  @Test
  public void shouldGiveErrorIfFacilityDoesNotExist() throws Exception {
    when(requisitionGroupRepository.getByCode(requisitionGroupMember.getRequisitionGroup())).thenReturn(requisitionGroup);
    when(facilityRepository.getIdForCode(requisitionGroupMember.getFacility().getCode())).thenThrow(new DataException("Invalid Facility Code"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Facility Code");

    service.save(requisitionGroupMember);
  }

  @Test
  public void shouldGiveErrorIfNoProgramsMappedToRG() throws Exception {
    when(requisitionGroupRepository.getByCode(requisitionGroupMember.getRequisitionGroup())).thenReturn(requisitionGroup);
    when(facilityRepository.getIdForCode(requisitionGroupMember.getFacility().getCode())).thenReturn(FACILITY_ID);

    when(requisitionGroupProgramScheduleRepository.getProgramIDsForRequisitionGroup(RG_ID)).thenReturn(new ArrayList<Long>());

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("No Program(s) mapped for Requisition Group");

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
}
