/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@Category(UnitTests.class)
public class RequisitionGroupHandlerTest {

  public static final Long USER = 1L;
  RequisitionGroupHandler requisitionGroupHandler;

  @Mock
  RequisitionGroupService requisitionGroupService;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    requisitionGroupHandler = new RequisitionGroupHandler(requisitionGroupService);
  }

  @Test
  public void shouldSaveRequisitionGroupWithModifiedByAndModifiedDateSet() throws Exception {
    RequisitionGroup requisitionGroup = new RequisitionGroup();
    requisitionGroup.setModifiedBy(USER);

    requisitionGroupHandler.save(requisitionGroup);

    verify(requisitionGroupService).save(requisitionGroup);
  }

}
