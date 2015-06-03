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
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InOrder;
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
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.defaultSupervisoryNode;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@Category(UnitTests.class)
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

  private static final Long userId = 1L;

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

    List<RequisitionGroup> requisitionGroupList = requisitionGroupService.search(searchParam, "requisitionGroup",
      pagination);

    verify(requisitionGroupRepository).searchByGroupName(searchParam, pagination);
    assertThat(requisitionGroupList, is(requisitionGroups));
  }

  @Test
  public void shouldSearchRequisitionGroupByNodeName() throws Exception {
    String searchParam = "searchParam";
    Pagination pagination = new Pagination(0, 0);

    List<RequisitionGroup> requisitionGroups = asList(new RequisitionGroup());
    when(requisitionGroupRepository.searchByNodeName(searchParam, pagination)).thenReturn(requisitionGroups);

    List<RequisitionGroup> requisitionGroupList = requisitionGroupService.search(searchParam, "supervisoryNode",
      pagination);

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
    RequisitionGroup requisitionGroup = new RequisitionGroup();

    requisitionGroupService.saveRequisitionGroupMembers(requisitionGroupMembers, requisitionGroup, userId);

    assertThat(requisitionGroupMembers.get(0).getRequisitionGroup(), is(requisitionGroup));
    assertThat(requisitionGroupMembers.get(0).getCreatedBy(), is(userId));
    assertThat(requisitionGroupMembers.get(0).getModifiedBy(), is(userId));
    verify(requisitionGroupMemberService).save(requisitionGroupMembers.get(0));
  }

  @Test
  public void shouldSaveRequisitionGroupProgramSchedules() {
    List<RequisitionGroupProgramSchedule> requisitionGroupProgramSchedules = asList(
      new RequisitionGroupProgramSchedule());

    RequisitionGroup requisitionGroup = new RequisitionGroup();
    requisitionGroupService.saveRequisitionGroupProgramSchedules(requisitionGroupProgramSchedules, requisitionGroup,
      userId);

    assertThat(requisitionGroupProgramSchedules.get(0).getId(), is(nullValue()));
    assertThat(requisitionGroupProgramSchedules.get(0).getRequisitionGroup(), is(requisitionGroup));
    assertThat(requisitionGroupProgramSchedules.get(0).getCreatedBy(), is(userId));
    assertThat(requisitionGroupProgramSchedules.get(0).getModifiedBy(), is(userId));
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

    spyRequisitionGroup.updateWithMembersAndSchedules(requisitionGroup, requisitionGroupMembers,
      requisitionGroupProgramSchedules, userId);

    verify(spyRequisitionGroup).save(requisitionGroup);
    verify(requisitionGroupMemberService).deleteMembersForGroup(requisitionGroup.getId());
    verify(requisitionGroupMemberService, times(2)).insert(Matchers.any(RequisitionGroupMember.class));
    verify(requisitionGroupProgramScheduleService).deleteRequisitionGroupProgramSchedulesFor(requisitionGroup.getId());
    verify(requisitionGroupProgramScheduleService, times(2)).save(Matchers.any(RequisitionGroupProgramSchedule.class));

    assertThat(requisitionGroup.getModifiedBy(), is(userId));

    assertThat(requisitionGroupProgramSchedule1.getRequisitionGroup(), is(requisitionGroup));
    assertThat(requisitionGroupProgramSchedule1.getModifiedBy(), is(userId));
    assertNull(requisitionGroupProgramSchedule1.getId());
    assertThat(requisitionGroupProgramSchedule2.getRequisitionGroup(), is(requisitionGroup));
    assertThat(requisitionGroupProgramSchedule2.getModifiedBy(), is(userId));

    assertThat(member1.getRequisitionGroup(), is(requisitionGroup));
    assertThat(member1.getCreatedBy(), is(userId));
    assertThat(member1.getModifiedBy(), is(userId));
    assertThat(member2.getRequisitionGroup(), is(requisitionGroup));
    assertThat(member2.getCreatedBy(), is(userId));
    assertThat(member2.getModifiedBy(), is(userId));
  }

  @Test
  public void shouldSaveWithMembersAndSchedules() {
    RequisitionGroup requisitionGroup = new RequisitionGroup();
    ArrayList<RequisitionGroupMember> requisitionGroupMembers = new ArrayList<>();
    ArrayList<RequisitionGroupProgramSchedule> requisitionGroupProgramSchedules = new ArrayList<>();

    RequisitionGroupService spyRequisitionGroupService = PowerMockito.spy(requisitionGroupService);
    doNothing().when(spyRequisitionGroupService).save(requisitionGroup);
    doNothing().when(spyRequisitionGroupService).saveRequisitionGroupProgramSchedules(requisitionGroupProgramSchedules,
      requisitionGroup, userId);
    doNothing().when(spyRequisitionGroupService).saveRequisitionGroupMembers(requisitionGroupMembers, requisitionGroup,
      userId);
    InOrder order = inOrder(spyRequisitionGroupService);

    spyRequisitionGroupService.saveWithMembersAndSchedules(requisitionGroup, requisitionGroupMembers,
      requisitionGroupProgramSchedules, userId);

    assertThat(requisitionGroup.getCreatedBy(), is(userId));
    order.verify(spyRequisitionGroupService).save(requisitionGroup);
    order.verify(spyRequisitionGroupService).saveRequisitionGroupProgramSchedules(requisitionGroupProgramSchedules,
      requisitionGroup, userId);
    order.verify(spyRequisitionGroupService).saveRequisitionGroupMembers(requisitionGroupMembers, requisitionGroup,
      userId);
  }
}
