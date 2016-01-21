/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.RequisitionGroupProgramScheduleService;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.form.RequisitionGroupFormDTO;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.controller.RequisitionGroupController.SEARCH_PAGE_SIZE;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RequisitionGroupController.class)
public class RequisitionGroupControllerTest {

  private static final Long userId = 1L;

  @Mock
  private RequisitionGroupService requisitionGroupService;

  @Mock
  private StaticReferenceDataService staticReferenceDataService;

  @Mock
  private MessageService messageService;

  @Mock
  private RequisitionGroupProgramScheduleService requisitionGroupProgramScheduleService;

  @InjectMocks
  private RequisitionGroupController requisitionGroupController;

  private MockHttpServletRequest request;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    request = new MockHttpServletRequest();
    request.getSession().setAttribute("USER_ID", userId);
  }

  @Test
  public void shouldSearchRequisitionGroup() throws Exception {
    String searchParam = "searchParam";
    String columnName = "columnName";
    Integer pageNumber = 1;
    Integer pageSize = 10;
    List<RequisitionGroup> requisitionGroups = asList(new RequisitionGroup());

    when(staticReferenceDataService.getPropertyValue(SEARCH_PAGE_SIZE)).thenReturn(String.valueOf(pageSize));
    Pagination pagination = new Pagination(pageNumber, pageSize);
    whenNew(Pagination.class).withArguments(pageNumber, pageSize).thenReturn(pagination);
    when(requisitionGroupService.search(searchParam, columnName, pagination)).thenReturn(requisitionGroups);
    when(requisitionGroupService.getTotalRecords(searchParam, columnName)).thenReturn(5);

    ResponseEntity<OpenLmisResponse> response = requisitionGroupController.search(searchParam, columnName, pageNumber);

    verify(requisitionGroupService).search(searchParam, columnName, pagination);
    verify(staticReferenceDataService).getPropertyValue(SEARCH_PAGE_SIZE);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat((List<RequisitionGroup>) response.getBody().getData().get("requisitionGroupList"),
      is(requisitionGroups));
    pagination.setTotalRecords(5);
    assertThat((Pagination) response.getBody().getData().get("pagination"), is(pagination));
  }

  @Test
  public void shouldGetRequisitionGroupWithMemberById() throws Exception {
    RequisitionGroup requisitionGroup = new RequisitionGroup();
    Long requisitionGroupId = 1L;
    List<RequisitionGroupMember> requisitionGroupMembers = asList(new RequisitionGroupMember());
    List<RequisitionGroupProgramSchedule> requisitionGroupProgramSchedules = asList(
      new RequisitionGroupProgramSchedule());

    when(requisitionGroupService.getBy(requisitionGroupId)).thenReturn(requisitionGroup);
    when(requisitionGroupService.getMembersBy(requisitionGroupId)).thenReturn(requisitionGroupMembers);
    when(requisitionGroupProgramScheduleService.getByRequisitionGroupId(requisitionGroupId)).thenReturn(
      requisitionGroupProgramSchedules);

    ResponseEntity<OpenLmisResponse> response = requisitionGroupController.getById(requisitionGroupId);

    verify(requisitionGroupService).getBy(requisitionGroupId);
    verify(requisitionGroupService).getMembersBy(requisitionGroupId);
    verify(requisitionGroupProgramScheduleService).getByRequisitionGroupId(requisitionGroupId);
    assertThat(
      ((RequisitionGroupFormDTO) response.getBody().getData().get("requisitionGroupData")).getRequisitionGroup(),
      is(requisitionGroup));
    assertThat(
      ((RequisitionGroupFormDTO) response.getBody().getData().get("requisitionGroupData")).getRequisitionGroupMembers(),
      is(requisitionGroupMembers));
  }

  @Test
  public void shouldInsertRequisitionGroup() {
    RequisitionGroupFormDTO requisitionGroupFormDTO = new RequisitionGroupFormDTO(new RequisitionGroup(),
      asList(new RequisitionGroupMember()),
      asList(new RequisitionGroupProgramSchedule()));

    when(messageService.message("message.requisition.group.created.success", (Object) null)).thenReturn("save success");

    ResponseEntity<OpenLmisResponse> response = requisitionGroupController.insert(requisitionGroupFormDTO, request);

    verify(requisitionGroupService).saveWithMembersAndSchedules(requisitionGroupFormDTO.getRequisitionGroup(),
      requisitionGroupFormDTO.getRequisitionGroupMembers(),
      requisitionGroupFormDTO.getRequisitionGroupProgramSchedules(), userId);

    assertThat(response.getBody().getSuccessMsg(), is("save success"));
    assertThat((Long) response.getBody().getData().get("requisitionGroupId"),
      is(requisitionGroupFormDTO.getRequisitionGroup().getId()));
  }

  @Test
  public void shouldUpdateRequisitionGroup() {
    RequisitionGroupFormDTO requisitionGroupFormDTO = new RequisitionGroupFormDTO(new RequisitionGroup(),
      asList(new RequisitionGroupMember()),
      asList(new RequisitionGroupProgramSchedule()));

    when(messageService.message("message.requisition.group.updated.success", (Object) null)).thenReturn(
      "updated success");

    ResponseEntity<OpenLmisResponse> response = requisitionGroupController.update(1L, requisitionGroupFormDTO, request);

    verify(requisitionGroupService).updateWithMembersAndSchedules(requisitionGroupFormDTO.getRequisitionGroup(),
      requisitionGroupFormDTO.getRequisitionGroupMembers(),
      requisitionGroupFormDTO.getRequisitionGroupProgramSchedules(), userId);
    assertThat(response.getBody().getSuccessMsg(), is("updated success"));
    assertThat((Long) response.getBody().getData().get("requisitionGroupId"), is(1L));
  }
}
