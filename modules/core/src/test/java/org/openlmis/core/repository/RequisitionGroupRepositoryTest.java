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
import org.mockito.Mock;
import org.openlmis.core.builder.RequisitionGroupBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.RequisitionGroupMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@Category(UnitTests.class)
public class RequisitionGroupRepositoryTest {

  RequisitionGroupRepository repository;
  RequisitionGroup requisitionGroup;

  @Mock
  private RequisitionGroupMapper mapper;

  @Mock
  private CommaSeparator commaSeparator;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setUp() {
    initMocks(this);
    repository = new RequisitionGroupRepository(mapper, commaSeparator);
    requisitionGroup = make(a(RequisitionGroupBuilder.defaultRequisitionGroup));
    requisitionGroup.setSupervisoryNode(new SupervisoryNode());
  }

  @Test
  public void shouldSaveRequisitionGroup() {
    repository.insert(requisitionGroup);
    verify(mapper).insert(requisitionGroup);
  }

  @Test
  public void shouldThrowExceptionIfDuplicateCodeBeingInserted() {
    doThrow(new DuplicateKeyException("duplicate code")).when(mapper).insert(requisitionGroup);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.duplicate.code.requisition.group");

    repository.insert(requisitionGroup);
  }

  @Test
  public void shouldUpdateRequisitionGroup() {
    repository.update(requisitionGroup);
    verify(mapper).update(requisitionGroup);
  }

  @Test
  public void shouldThrowExceptionIfDuplicateCodeBeingUpdated() {
    doThrow(new DuplicateKeyException("duplicate code")).when(mapper).update(requisitionGroup);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.duplicate.code.requisition.group");

    repository.update(requisitionGroup);
  }

  @Test
  public void shouldGetRequisitionGroupForSupervisoryNodes() {
    List<SupervisoryNode> supervisoryNodes = new ArrayList<>();
    when(commaSeparator.commaSeparateIds(supervisoryNodes)).thenReturn("{1, 2}");
    List<RequisitionGroup> requisitionGroups = new ArrayList<>();
    when(mapper.getRequisitionGroupBySupervisoryNodes("{1, 2}")).thenReturn(requisitionGroups);
    List<RequisitionGroup> result = repository.getRequisitionGroups(supervisoryNodes);
    verify(mapper).getRequisitionGroupBySupervisoryNodes("{1, 2}");
    assertThat(result, is(requisitionGroups));
  }

  @Test
  public void shouldGetRequisitionGroupForFacilityAndProgram() {
    Facility facility = new Facility(1L);
    Program program = new Program(1L);
    repository.getRequisitionGroupForProgramAndFacility(program, facility);
    verify(mapper).getRequisitionGroupForProgramAndFacility(program, facility);
  }
}
