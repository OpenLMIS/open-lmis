/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupMemberMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupProgramScheduleMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
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

  @Mock
  private RequisitionGroupRepository requisitionGroupRepository;

  private RequisitionGroupMemberRepository repository;
  private Facility facility;

  @Before
  public void setUp() throws Exception {
    facility = make(a(defaultFacility));
    requisitionGroup = make(a(defaultRequisitionGroup));

    requisitionGroupMember = new RequisitionGroupMember();
    requisitionGroupMember.setRequisitionGroup(requisitionGroup);
    requisitionGroupMember.setFacility(facility);

    programIdList = new ArrayList<>();
    programIdList.add(1);

    initMocks(this);
    repository = new RequisitionGroupMemberRepository(requisitionGroupMemberMapper
    );
  }

  @Test
  public void shouldSaveMappingIfMappingDoesNotExist() throws Exception {
    repository.insert(requisitionGroupMember);

    verify(requisitionGroupMemberMapper).insert(requisitionGroupMember);
  }

  @Test
  public void shouldSaveMappingIfMappingAlreadyExists() throws Exception {
    doThrow(DataIntegrityViolationException.class).when(requisitionGroupMemberMapper).insert(requisitionGroupMember);

    expectedEx.expect(dataExceptionMatcher("error.facility.requisition.group.mapping.exists"));

    repository.insert(requisitionGroupMember);

    verify(requisitionGroupMemberMapper, never()).insert(requisitionGroupMember);
  }

  @Test
  public void shouldGetMappingForRequisitionGroupIdAndFacilityId() throws Exception {
    requisitionGroup.setId(5L);
    facility.setId(4L);
    when(requisitionGroupMemberMapper.
      getMappingByRequisitionGroupIdAndFacilityId(requisitionGroup, facility)).
      thenReturn(requisitionGroupMember);

    RequisitionGroupMember returnedRGMember = repository.
      getRequisitionGroupMemberForRequisitionGroupIdAndFacilityId(requisitionGroup, facility);

    assertThat(returnedRGMember, is(requisitionGroupMember));
  }

  @Test
  public void shouldGetAllRequisitionGroupMembersByFacilityId() throws Exception {
    Long facilityId = 4L;
    List<RequisitionGroupMember> expectedMembers = asList(new RequisitionGroupMember());
    when(requisitionGroupMemberMapper.getAllRequisitionGroupMembersByFacility(facilityId)).thenReturn(expectedMembers);

    List<RequisitionGroupMember> actualMembers = repository.getAllRequisitionGroupMembersByFacility(facilityId);

    verify(requisitionGroupMemberMapper).getAllRequisitionGroupMembersByFacility(facilityId);
    assertThat(actualMembers, is(expectedMembers));
  }

  @Test
  public void shouldDeleteRequisitionGroupToFacilityMapping() {
    repository.deleteMembersFor(facility);
    verify(requisitionGroupMemberMapper).deleteMembersFor(facility);
  }
}

