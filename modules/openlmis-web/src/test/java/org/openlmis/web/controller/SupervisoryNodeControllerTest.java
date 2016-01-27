/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.web.controller.SupervisoryNodeController.SUPERVISORY_NODES;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class SupervisoryNodeControllerTest {

  @Mock
  private SupervisoryNodeService supervisoryNodeService;

  @Mock
  MessageService messageService;

  @InjectMocks
  private SupervisoryNodeController controller;

  public static final String USER_ID = "USER_ID";
  private MockHttpServletRequest request = new MockHttpServletRequest();

  @Before
  public void setUp() {
    setLoggedInUserId();
  }

  @Test
  public void shouldGetAllSupervisoryNodes() {
    List<SupervisoryNode> expectedSupervisoryNodes = new ArrayList<>();
    when(supervisoryNodeService.getAll()).thenReturn(expectedSupervisoryNodes);

    ResponseEntity<OpenLmisResponse> response = controller.getAll();

    verify(supervisoryNodeService).getAll();
    List<SupervisoryNode> actual = (List<SupervisoryNode>) response.getBody().getData().get(SUPERVISORY_NODES);
    assertThat(actual, is(expectedSupervisoryNodes));
  }

  @Test
  public void shouldGetSupervisoryNodeById() {
    Long id = 1L;
    SupervisoryNode expectedNode = new SupervisoryNode();
    when(supervisoryNodeService.getSupervisoryNode(id)).thenReturn(expectedNode);

    SupervisoryNode actualNode = controller.getById(id);

    verify(supervisoryNodeService).getSupervisoryNode(id);
    assertThat(actualNode, is(expectedNode));
  }

  @Test
  public void shouldGetFilteredNodesForRequestedQuery() {
    String query = "Node1";
    List<SupervisoryNode> supervisoryNodes = new ArrayList<>();
    when(supervisoryNodeService.getFilteredSupervisoryNodesByName(query)).thenReturn(supervisoryNodes);

    List<SupervisoryNode> filteredNodes = controller.getFilteredNodes(query);

    verify(supervisoryNodeService).getFilteredSupervisoryNodesByName(query);
    assertThat(filteredNodes, is(supervisoryNodes));
  }

  @Test
  public void shouldInsertSupervisoryNodeSuccessfully() {
    SupervisoryNode supervisoryNode = new SupervisoryNode(1L);
    supervisoryNode.setName("Node 1");
    when(messageService.message("message.supervisory.node.created.success", supervisoryNode.getName())).thenReturn("success");

    ResponseEntity<OpenLmisResponse> responseEntity = controller.insert(supervisoryNode, request);

    verify(supervisoryNodeService).save(supervisoryNode);
    assertThat((Long) responseEntity.getBody().getData().get("supervisoryNodeId"), is(supervisoryNode.getId()));
    assertThat(responseEntity.getBody().getSuccessMsg(), is("success"));
    assertThat(supervisoryNode.getCreatedBy(), is(1L));
    assertThat(supervisoryNode.getModifiedBy(), is(1L));
  }

  @Test
  public void shouldReturnErrorMessageWhenExceptionOccursOnSupervisoryNodeInsert() {
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    doThrow(new DataException("error")).when(supervisoryNodeService).save(supervisoryNode);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.insert(supervisoryNode, request);

    verify(supervisoryNodeService).save(supervisoryNode);
    verify(messageService, never()).message(anyString(), anyString());
    assertThat(responseEntity.getBody().getErrorMsg(), is("error"));
    assertThat(supervisoryNode.getCreatedBy(), is(1L));
    assertThat(supervisoryNode.getModifiedBy(), is(1L));
  }

  @Test
  public void shouldUpdateSupervisoryNodeSuccessfully() {
    SupervisoryNode supervisoryNode = new SupervisoryNode(1L);
    supervisoryNode.setName("Node 1");
    when(messageService.message("message.supervisory.node.updated.success", supervisoryNode.getName())).thenReturn(
      "success");

    ResponseEntity<OpenLmisResponse> responseEntity = controller.update(supervisoryNode, supervisoryNode.getId(),
      request);

    verify(supervisoryNodeService).save(supervisoryNode);
    assertThat((Long) responseEntity.getBody().getData().get("supervisoryNodeId"), is(supervisoryNode.getId()));
    assertThat(responseEntity.getBody().getSuccessMsg(), is("success"));
    assertThat(supervisoryNode.getModifiedBy(), is(1L));
  }

  @Test
  public void shouldReturnErrorMessageWhenExceptionOccursOnSupervisoryNodeUpdate() {
    SupervisoryNode supervisoryNode = new SupervisoryNode(1L);
    doThrow(new DataException("error")).when(supervisoryNodeService).save(supervisoryNode);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.update(supervisoryNode, 1L, request);

    verify(supervisoryNodeService).save(supervisoryNode);
    verify(messageService, never()).message(anyString(), anyString());
    assertThat(responseEntity.getBody().getErrorMsg(), is("error"));
    assertThat(supervisoryNode.getModifiedBy(), is(1L));
  }

  @Test
  public void shouldGetTopLevelNodesForRequestedQuery() {
    String query = "Node1";
    List<SupervisoryNode> supervisoryNodes = new ArrayList<>();
    when(supervisoryNodeService.searchTopLevelSupervisoryNodesByName(query)).thenReturn(supervisoryNodes);

    List<SupervisoryNode> filteredNodes = controller.searchTopLevelSupervisoryNodesByName(query);

    verify(supervisoryNodeService).searchTopLevelSupervisoryNodesByName(query);
    assertThat(filteredNodes, is(supervisoryNodes));
  }

  private void setLoggedInUserId() {
    request.getSession().setAttribute(USER_ID, 1L);
  }
}
