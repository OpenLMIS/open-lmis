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
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.controller.SupplyLineController.PAGINATION;
import static org.openlmis.web.controller.SupplyLineController.SUPPLY_LINES;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(SupplyLineController.class)
public class SupplyLineControllerTest {

  @Mock
  SupplyLineService service;

  @InjectMocks
  SupplyLineController controller;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void shouldSearchSupplyLines() throws Exception {
    String searchParam = "supply";
    String column = "name";
    Integer page = 2;
    String limit = "10";
    List<SupplyLine> supplyLines = asList(new SupplyLine());

    Pagination pagination = new Pagination(2, 3);
    whenNew(Pagination.class).withArguments(page, Integer.parseInt(limit)).thenReturn(pagination);
    when(service.search(searchParam, column, pagination)).thenReturn(supplyLines);
    when(service.getTotalSearchResultCount(searchParam, column)).thenReturn(3);

    ResponseEntity<OpenLmisResponse> response = controller.search(searchParam, column, page, limit);

    assertThat((List<SupplyLine>) response.getBody().getData().get(SUPPLY_LINES), is(supplyLines));
    assertThat((Pagination) response.getBody().getData().get(PAGINATION), is(pagination));

  }
}