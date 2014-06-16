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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.RequisitionGroupRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.db.categories.UnitTests;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.defaultSupervisoryNode;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(RequisitionGroupService.class)
public class RequisitionGroupServiceTest {

  @InjectMocks
  private RequisitionGroupService requisitionGroupService;

  @Mock
  private RequisitionGroupRepository requisitionGroupRepository;

  @Mock
  private SupervisoryNodeRepository supervisoryNodeRepository;

  @Mock
  private RequisitionGroupMemberService requisitionGroupMemberService;

  @Mock
  private RequisitionGroupProgramScheduleService requisitionGroupProgramScheduleService;

  @Test
  public void shouldSaveANewRequisitionGroup() {
    SupervisoryNode supervisoryNode = make(a(defaultSupervisoryNode));
    RequisitionGroup requisitionGroup = make(a(defaultRequisitionGroup));
    requisitionGroup.setSupervisoryNode(supervisoryNode);

    requisitionGroupService.save(requisitionGroup);

    verify(supervisoryNodeRepository).getIdForCode(supervisoryNode.getCode());
    verify(requisitionGroupRepository).insert(requisitionGroup);
  }

  @Test
  public void shouldUpdateExistingRequisitionGroup() {
    SupervisoryNode supervisoryNode = make(a(defaultSupervisoryNode));
    RequisitionGroup requisitionGroup = make(a(defaultRequisitionGroup));
    requisitionGroup.setId(1L);
    requisitionGroup.setSupervisoryNode(supervisoryNode);

    requisitionGroupService.save(requisitionGroup);

    verify(supervisoryNodeRepository).getIdForCode(supervisoryNode.getCode());
    verify(requisitionGroupRepository).update(requisitionGroup);
  }

  @Test
  public void shouldSearchRequisitionGroupByGroupName() throws Exception {
    String searchParam = "searchParam";
    Pagination pagination = new Pagination(0, 0);

    List<RequisitionGroup> requisitionGroups = asList(new RequisitionGroup());
    when(requisitionGroupRepository.searchByGroupName(searchParam, pagination)).thenReturn(requisitionGroups);

    List<RequisitionGroup> requisitionGroupList = requisitionGroupService.search(searchParam, "requisitionGroup", pagination);

    verify(requisitionGroupRepository).searchByGroupName(searchParam, pagination);
    assertThat(requisitionGroupList, is(requisitionGroups));
  }

  @Test
  public void shouldSearchRequisitionGroupByNodeName() throws Exception {
    String searchParam = "searchParam";
    Pagination pagination = new Pagination(0, 0);

    List<RequisitionGroup> requisitionGroups = asList(new RequisitionGroup());
    when(requisitionGroupRepository.searchByNodeName(searchParam, pagination)).thenReturn(requisitionGroups);

    List<RequisitionGroup> requisitionGroupList = requisitionGroupService.search(searchParam, "supervisoryNode", pagination);

    verify(requisitionGroupRepository).searchByNodeName(searchParam, pagination);
    assertThat(requisitionGroupList, is(requisitionGroups));
  }

  @Test
  public void shouldGetResultCountSearchByGroupName() throws Exception {
    String searchParam = "searchParam";
    int resultCount = 5;

    when(requisitionGroupRepository.getTotalRecordsForSearchOnGroupName(searchParam)).thenReturn(resultCount);

    Integer count = requisitionGroupService.getTotalRecords(searchParam, "requisitionGroup");

    verify(requisitionGroupRepository).getTotalRecordsForSearchOnGroupName(searchParam);
    assertThat(count, is(resultCount));
  }

  @Test
  public void shouldGetResultCountSearchByNodeName() throws Exception {
    String searchParam = "searchParam";
    int resultCount = 4;

    when(requisitionGroupRepository.getTotalRecordsForSearchOnNodeName(searchParam)).thenReturn(resultCount);

    Integer count = requisitionGroupService.getTotalRecords(searchParam, "supervisoryNode");

    verify(requisitionGroupRepository).getTotalRecordsForSearchOnNodeName(searchParam);
    assertThat(count, is(resultCount));
  }

  @Test
  public void shouldSaveRequisitionGroupMembers() throws Exception {
    List<RequisitionGroupMember> requisitionGroupMembers = asList(new RequisitionGroupMember());

    requisitionGroupService.saveRequisitionGroupMembers(requisitionGroupMembers, new RequisitionGroup());

    verify(requisitionGroupMemberService).save(requisitionGroupMembers.get(0));
  }

  @Test
  public void shouldSaveRequisitionGroupProgramSchedules() {
    List<RequisitionGroupProgramSchedule> requisitionGroupProgramSchedules = asList(new RequisitionGroupProgramSchedule());

    requisitionGroupService.saveRequisitionGroupProgramSchedules(requisitionGroupProgramSchedules, new RequisitionGroup());

    verify(requisitionGroupProgramScheduleService).save(requisitionGroupProgramSchedules.get(0));
  }

  @Test
  public void shouldUpdateWithRequisitionGroupMembers() {
    RequisitionGroup requisitionGroup = new RequisitionGroup();
    requisitionGroup.setId(1L);
    requisitionGroup.setCode("RG1");
    requisitionGroup.setModifiedBy(2L);

    RequisitionGroupMember member1 = new RequisitionGroupMember();
    member1.setId(1L);
    RequisitionGroupMember member2 = new RequisitionGroupMember();
    List<RequisitionGroupMember> requisitionGroupMembers = new ArrayList<>();
    requisitionGroupMembers.add(member1);
    requisitionGroupMembers.add(member2);

    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule1 = new RequisitionGroupProgramSchedule();
    requisitionGroupProgramSchedule1.setId(2L);
    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule2 = new RequisitionGroupProgramSchedule();
    List<RequisitionGroupProgramSchedule> requisitionGroupProgramSchedules = new ArrayList<>();
    requisitionGroupProgramSchedules.add(requisitionGroupProgramSchedule1);
    requisitionGroupProgramSchedules.add(requisitionGroupProgramSchedule2);

    RequisitionGroupService spyRequisitionGroup = PowerMockito.spy(requisitionGroupService);
    doNothing().when(spyRequisitionGroup).save(requisitionGroup);

    spyRequisitionGroup.updateWithMembersAndSchedules(requisitionGroup, requisitionGroupMembers, requisitionGroupProgramSchedules);

    verify(spyRequisitionGroup).save(requisitionGroup);
    verify(requisitionGroupMemberService).deleteMembersForGroup(requisitionGroup.getId());
    verify(requisitionGroupMemberService, times(2)).insert(Matchers.any(RequisitionGroupMember.class));
    verify(requisitionGroupProgramScheduleService).deleteRequisitionGroupProgramSchedulesFor(requisitionGroup.getId());
    verify(requisitionGroupProgramScheduleService, times(2)).save(Matchers.any(RequisitionGroupProgramSchedule.class));

    assertThat(requisitionGroupProgramSchedule1.getRequisitionGroup(), is(requisitionGroup));
    assertThat(requisitionGroupProgramSchedule1.getModifiedBy(), is(requisitionGroup.getModifiedBy()));
    assertNull(requisitionGroupProgramSchedule1.getId());
    assertThat(requisitionGroupProgramSchedule2.getRequisitionGroup(), is(requisitionGroup));
    assertThat(requisitionGroupProgramSchedule2.getModifiedBy(), is(requisitionGroup.getModifiedBy()));

    assertThat(member1.getRequisitionGroup(), is(requisitionGroup));
    assertThat(member1.getModifiedBy(), is(requisitionGroup.getModifiedBy()));
    assertThat(member2.getRequisitionGroup(), is(requisitionGroup));
    assertThat(member2.getModifiedBy(), is(requisitionGroup.getModifiedBy()));
  }
}
