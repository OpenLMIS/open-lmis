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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class SupervisoryNodeControllerTest {

  @Mock
  private SupervisoryNodeService supervisoryNodeService;

  @InjectMocks
  private SupervisoryNodeController controller;


  @Test
  public void shouldGetAllSupervisoryNodes() throws Exception {
    List<SupervisoryNode> expectedSupervisoryNodes = new ArrayList<>();
    when(supervisoryNodeService.getAll()).thenReturn(expectedSupervisoryNodes);

    ResponseEntity<OpenLmisResponse> response = controller.getAll();

    verify(supervisoryNodeService).getAll();
    List<SupervisoryNode> actual = (List<SupervisoryNode>) response.getBody().getData().get(SupervisoryNodeController.SUPERVISORY_NODES);
    assertThat(actual, is(expectedSupervisoryNodes));
  }
}
