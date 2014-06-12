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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.RequisitionGroupRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.defaultSupervisoryNode;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RequisitionGroupServiceTest {

  @InjectMocks
  private RequisitionGroupService requisitionGroupService;

  @Mock
  private RequisitionGroupRepository requisitionGroupRepository;

  @Mock
  private SupervisoryNodeRepository supervisoryNodeRepository;

  @Mock
  private RequisitionGroupMemberService requisitionGroupMemberService;

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
}
