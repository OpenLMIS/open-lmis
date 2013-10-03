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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.RequisitionGroupRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.RequisitionGroupBuilder.defaultRequisitionGroup;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.defaultSupervisoryNode;

@Category(UnitTests.class)
public class RequisitionGroupServiceTest {

  private RequisitionGroupService requisitionGroupService;

  @Mock
  private RequisitionGroupRepository requisitionGroupRepository;

  @Mock
  private SupervisoryNodeRepository supervisoryNodeRepository;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    requisitionGroupService = new RequisitionGroupService(requisitionGroupRepository, supervisoryNodeRepository);
  }

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
}
