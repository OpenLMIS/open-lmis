/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.RequisitionGroupBuilder.code;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;


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
  private SupervisoryNode supervisoryNodeWithParent;

  @Before
  public void setUp() throws Exception {
    supervisoryNodeWithParent = new SupervisoryNode();
    supervisoryNodeWithParent.setId(10);
    supervisoryNodeWithParent.setFacility(new Facility());
    SupervisoryNode parent = new SupervisoryNode();
    parent.setCode("PSN");
    parent.setId(20);
    supervisoryNodeWithParent.setParent(parent);
    repository = new SupervisoryNodeRepository(supervisoryNodeMapper, facilityRepository, requisitionGroupRepository);
  }

  @Test
  public void shouldGiveErrorIfDuplicateCodeFound() throws Exception {
    doThrow(new DuplicateKeyException("")).when(supervisoryNodeMapper).insert(supervisoryNodeWithParent);
    when(supervisoryNodeMapper.getSupervisoryNode(10)).thenReturn(supervisoryNodeWithParent);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate SupervisoryNode Code");

    repository.save(supervisoryNodeWithParent);

    verify(supervisoryNodeMapper).insert(supervisoryNodeWithParent);
  }

  @Test
  public void shouldGiveErrorIfParentNodeCodeDoesNotExist() throws Exception {
    when(supervisoryNodeMapper.getIdForCode(supervisoryNodeWithParent.getParent().getCode())).thenThrow(new DataException("Invalid SupervisoryNode Code"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Supervisory Node Parent does not exist");

    repository.save(supervisoryNodeWithParent);

    verify(supervisoryNodeMapper).getIdForCode(supervisoryNodeWithParent.getParent().getCode());
  }

  @Test
  public void shouldGiveErrorIfFacilityCodeDoesNotExist() throws Exception {
    when(supervisoryNodeMapper.getIdForCode(supervisoryNodeWithParent.getParent().getCode())).thenReturn(1);
    when(facilityRepository.getIdForCode(supervisoryNodeWithParent.getFacility().getCode())).thenThrow(new DataException("Invalid Facility Code"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Facility Code");

    repository.save(supervisoryNodeWithParent);

    verify(facilityRepository).getIdForCode(supervisoryNodeWithParent.getFacility().getCode());
    verify(supervisoryNodeMapper).getIdForCode(supervisoryNodeWithParent.getParent().getCode());
  }

  @Test
  public void shouldSaveSupervisoryNode() throws Exception {
    when(supervisoryNodeMapper.getIdForCode(supervisoryNodeWithParent.getParent().getCode())).thenReturn(supervisoryNodeWithParent.getParent().getId());
    when(facilityRepository.getIdForCode(supervisoryNodeWithParent.getFacility().getCode())).thenReturn(1);

    repository.save(supervisoryNodeWithParent);

    verify(facilityRepository).getIdForCode(supervisoryNodeWithParent.getFacility().getCode());
    assertThat(supervisoryNodeWithParent.getParent().getId(), is(20));
    assertThat(supervisoryNodeWithParent.getFacility().getId(), is(1));
    verify(supervisoryNodeMapper).insert(supervisoryNodeWithParent);
  }

  @Test
  public void shouldSaveSupervisoryNodeIfParentNotSupplied() throws Exception {
    when(facilityRepository.getIdForCode(supervisoryNodeWithParent.getFacility().getCode())).thenReturn(1);
    supervisoryNodeWithParent.setParent(null);
    repository.save(supervisoryNodeWithParent);

    verify(facilityRepository).getIdForCode(supervisoryNodeWithParent.getFacility().getCode());
    verify(supervisoryNodeMapper, never()).getIdForCode(anyString());
    assertThat(supervisoryNodeWithParent.getParent(), is(nullValue()));
    assertThat(supervisoryNodeWithParent.getFacility().getId(), is(1));
    verify(supervisoryNodeMapper).insert(supervisoryNodeWithParent);
  }

  @Test
  public void shouldReturnIdForTheGivenCode() {
    when(supervisoryNodeMapper.getIdForCode("ABC")).thenReturn(10);
    assertThat(repository.getIdForCode("ABC"), is(10));
  }

  @Test
  public void shouldThrowExceptionWhenCodeDoesNotExist() {
    when(supervisoryNodeMapper.getIdForCode("ABC")).thenReturn(null);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid SupervisoryNode Code");
    repository.getIdForCode("ABC");
  }

  @Test
  public void shouldReturnParentIdForASupervisoryNode() {
    when(supervisoryNodeMapper.getSupervisoryNode(10)).thenReturn(supervisoryNodeWithParent);

    supervisoryNodeWithParent.getParent().setId(null);
    assertThat(repository.getSupervisoryNodeParentId(10), is(nullValue()));

    supervisoryNodeWithParent.getParent().setId(20);
    assertThat(repository.getSupervisoryNodeParentId(10), is(20));
  }

  @Test
  public void shouldGetSupervisoryNodeForFacilityProgram() throws Exception {
    Facility facility = new Facility(1);
    Program program = new Program(1);
    SupervisoryNode expectedSupervisoryNode = new SupervisoryNode();
    RequisitionGroup requisitionGroup = make(a(defaultRequisitionGroup, with(code, "test code")));
    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(program, facility)).thenReturn(requisitionGroup);
    when(supervisoryNodeMapper.getFor(requisitionGroup.getCode())).thenReturn(expectedSupervisoryNode);

    SupervisoryNode actualSupervisoryNode = repository.getFor(facility, program);

    assertThat(actualSupervisoryNode, is(expectedSupervisoryNode));
  }

  @Test
  public void shouldReturnSupervisoryNodeAsNullWhenThereIsNoScheduleForAGivenRequisitionGroupAndProgram() throws Exception {
    Facility facility = new Facility(1);
    Program program = new Program(1);
    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(program, facility)).thenReturn(null);

    SupervisoryNode actualSupervisoryNode = repository.getFor(facility, program);

    assertThat(actualSupervisoryNode, is(nullValue()));
  }

  @Test
  public void shouldGetAllSupervisoryNodesInHierarchy() throws Exception {
    Integer userId = 1;
    Integer programId = 1;
    List<SupervisoryNode> expectedList = new ArrayList<>();
    when(supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyBy(userId, programId, "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}")).thenReturn(expectedList);
    List<SupervisoryNode> actualList = repository.getAllSupervisoryNodesInHierarchyBy(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION);
    verify(supervisoryNodeMapper).getAllSupervisoryNodesInHierarchyBy(userId, programId, "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}");
    assertThat(actualList, is(expectedList));
  }

  @Test
  public void shouldGetParentNodeForAGiveSupervisoryNode() throws Exception {
    SupervisoryNode expected = new SupervisoryNode();
    when(supervisoryNodeMapper.getParent(1)).thenReturn(expected);
    final SupervisoryNode actual = repository.getParent(1);
    verify(supervisoryNodeMapper).getParent(1);
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
    when(supervisoryNodeMapper.getAllSupervisoryNodesInHierarchyByUserAndRights(1, "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}")).thenReturn(expected);

    List<SupervisoryNode> actual = repository.getAllSupervisoryNodesInHierarchyBy(1, CREATE_REQUISITION, AUTHORIZE_REQUISITION);

    assertThat(actual, is(expected));
    verify(supervisoryNodeMapper).getAllSupervisoryNodesInHierarchyByUserAndRights(1, "{CREATE_REQUISITION, AUTHORIZE_REQUISITION}");
  }

  @Test
  public void shouldGetAllParentSupervisoryNodesInHierarchy() throws Exception {
    List<SupervisoryNode> expected = new ArrayList<>();
    SupervisoryNode supervisoryNode = new SupervisoryNode(1);
    when(supervisoryNodeMapper.getAllParentSupervisoryNodesInHierarchy(supervisoryNode)).thenReturn(expected);

    List<SupervisoryNode> actual = repository.getAllParentSupervisoryNodesInHierarchy(supervisoryNode);

    verify(supervisoryNodeMapper).getAllParentSupervisoryNodesInHierarchy(supervisoryNode);
    assertThat(actual, is(expected));
  }
}
