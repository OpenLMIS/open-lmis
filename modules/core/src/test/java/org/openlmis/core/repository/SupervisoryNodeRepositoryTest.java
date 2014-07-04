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
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.RequisitionGroupBuilder.code;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;
import static org.openlmis.core.domain.RightName.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.RightName.CREATE_REQUISITION;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class SupervisoryNodeRepositoryTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private SupervisoryNodeMapper supervisoryNodeMapper;
  @Mock
  private FacilityRepository facilityRepository;

  @Mock
  private RequisitionGroupRepository requisitionGroupRepository;

  private SupervisoryNodeRepository repository;
  private SupervisoryNode supervisoryNode;

  @Before
  public void setUp() throws Exception {
    supervisoryNode = new SupervisoryNode();
    supervisoryNode.setId(10L);
    supervisoryNode.setFacility(new Facility());
    SupervisoryNode parent = new SupervisoryNode();
    parent.setCode("PSN");
    parent.setId(20L);
    supervisoryNode.setParent(parent);
    repository = new SupervisoryNodeRepository(supervisoryNodeMapper, facilityRepository, requisitionGroupRepository);
  }

  @Test
  public void shouldInsertSupervisoryNode() throws Exception {
    repository.insert(supervisoryNode);
    verify(supervisoryNodeMapper).insert(supervisoryNode);
  }

  @Test
  public void shouldUpdateSupervisoryNode() throws Exception {
    repository.update(supervisoryNode);
    verify(supervisoryNodeMapper).update(supervisoryNode);
  }

  @Test
  public void shouldReturnIdForTheGivenCode() {
    when(supervisoryNodeMapper.getIdForCode("ABC")).thenReturn(10L);
    assertThat(repository.getIdForCode("ABC"), is(10L));
  }

  @Test
  public void shouldThrowExceptionWhenCodeDoesNotExist() {
    when(supervisoryNodeMapper.getIdForCode("ABC")).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.supervisory.node.invalid"));

    repository.getIdForCode("ABC");
  }

  @Test
  public void shouldReturnParentIdForASupervisoryNode() {
    when(supervisoryNodeMapper.getSupervisoryNode(10L)).thenReturn(supervisoryNode);

    supervisoryNode.getParent().setId(null);
    assertThat(repository.getSupervisoryNodeParentId(10L), is(nullValue()));

    supervisoryNode.getParent().setId(20L);
    assertThat(repository.getSupervisoryNodeParentId(10L), is(20L));
  }

  @Test
  public void shouldGetSupervisoryNodeForFacilityProgram() throws Exception {
    Facility facility = new Facility(1L);
    Program program = new Program(1L);
    SupervisoryNode expectedSupervisoryNode = new SupervisoryNode();
    RequisitionGroup requisitionGroup = make(a(defaultRequisitionGroup, with(code, "test code")));
    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(program, facility)).thenReturn(requisitionGroup);
    when(supervisoryNodeMapper.getFor(requisitionGroup.getCode())).thenReturn(expectedSupervisoryNode);

    SupervisoryNode actualSupervisoryNode = repository.getFor(facility, program);

    assertThat(actualSupervisoryNode, is(expectedSupervisoryNode));
  }

  @Test
  public void shouldReturnSupervisoryNodeAsNullWhenThereIsNoScheduleForAGivenRequisitionGroupAndProgram() throws Exception {
    Facility facility = new Facility(1L);
    Program program = new Program(1L);
    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(program, facility)).thenReturn(null);

    SupervisoryNode actualSupervisoryNode = repository.getFor(facility, program);

    assertThat(actualSupervisoryNode, is(nullValue()));
  }

  @Test
  public void shouldGetAllSupervisoryNodesInHierarchy() throws Exception {
    Long userId = 1L;
    Long programId = 1L;
    List<SupervisoryNode> expectedList = new ArrayList<>();
    when(supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyBy(userId, programId, "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}")).thenReturn(expectedList);
    List<SupervisoryNode> actualList = repository.getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION);
    verify(supervisoryNodeMapper).getAllSupervisoryNodesInHierarchyBy(userId, programId, "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}");
    assertThat(actualList, is(expectedList));
  }

  @Test
  public void shouldGetParentNodeForAGiveSupervisoryNode() throws Exception {
    SupervisoryNode expected = new SupervisoryNode();
    when(supervisoryNodeMapper.getParent(1L)).thenReturn(expected);
    final SupervisoryNode actual = repository.getParent(1L);
    verify(supervisoryNodeMapper).getParent(1L);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetAllSupervisoryNodes() throws Exception {
    List<SupervisoryNode> expected = new ArrayList<>();
    when(supervisoryNodeMapper.getAll()).thenReturn(expected);

    List<SupervisoryNode> actual = repository.getAll();

    verify(supervisoryNodeMapper).getAll();
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetSupervisoryNodesWithRightsForUser() throws Exception {
    List<SupervisoryNode> expected = new ArrayList<>();
    when(supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyByUserAndRights(1L, "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}")).thenReturn(expected);

    List<SupervisoryNode> actual = repository.getAllSupervisoryNodesInHierarchyBy(1L, CREATE_REQUISITION, AUTHORIZE_REQUISITION);

    assertThat(actual, is(expected));
    verify(supervisoryNodeMapper).getAllSupervisoryNodesInHierarchyByUserAndRights(1L, "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}");
  }

  @Test
  public void shouldGetAllParentSupervisoryNodesInHierarchy() throws Exception {
    List<SupervisoryNode> expected = new ArrayList<>();
    SupervisoryNode supervisoryNode = new SupervisoryNode(1L);
    when(supervisoryNodeMapper.getAllParentSupervisoryNodesInHierarchy(supervisoryNode)).thenReturn(expected);

    List<SupervisoryNode> actual = repository.getAllParentSupervisoryNodesInHierarchy(supervisoryNode);

    verify(supervisoryNodeMapper).getAllParentSupervisoryNodesInHierarchy(supervisoryNode);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldThrowIfDuplicateCodeInserted(){

    SupervisoryNode supervisoryNode = new SupervisoryNode();
    doThrow(new DuplicateKeyException("error_message")).when(supervisoryNodeMapper).insert(supervisoryNode);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.duplicate.code.supervisory.node");

    repository.insert(supervisoryNode);
  }

  @Test
  public void shouldThrowIfDuplicateCodeUpdated(){

    SupervisoryNode supervisoryNode = new SupervisoryNode();
    doThrow(new DuplicateKeyException("error_message")).when(supervisoryNodeMapper).update(supervisoryNode);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.duplicate.code.supervisory.node");

    repository.update(supervisoryNode);
  }
}
