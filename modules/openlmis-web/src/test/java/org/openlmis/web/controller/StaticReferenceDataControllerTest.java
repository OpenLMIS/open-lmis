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
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class StaticReferenceDataControllerTest {

  @Mock
  StaticReferenceDataService service;

  @InjectMocks
  StaticReferenceDataController staticReferenceDataController;

  @Test
  public void shouldGetPageSize() throws Exception {

    when(service.getPropertyValue(StaticReferenceDataController.LINE_ITEMS_PER_PAGE)).thenReturn("2");

    ResponseEntity<OpenLmisResponse> response = staticReferenceDataController.getPageSize();

    OpenLmisResponse openLmisResponse = response.getBody();
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat((String) openLmisResponse.getData().get("pageSize"), is("2"));
  }
}
