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

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.controller.SupplyLineController.PAGINATION;
import static org.openlmis.web.controller.SupplyLineController.SUPPLY_LINES;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(SupplyLineController.class)
public class SupplyLineControllerTest {

  @Mock
  SupplyLineService service;

  @Mock
  MessageService messageService;

  @InjectMocks
  SupplyLineController controller;

  public static final String USER_ID = "USER_ID";

  private MockHttpServletRequest request = new MockHttpServletRequest();

  @Before
  public void setUp() {
    initMocks(this);
    request = new MockHttpServletRequest();
    request.getSession().setAttribute("USER_ID", 1L);
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

  @Test
  public void shouldInsertSupplyLineSuccessfully() {
    SupplyLine supplyLine = new SupplyLine(1L);
    Mockito.when(messageService.message("message.supply.line.created.success")).thenReturn("success");

    ResponseEntity<OpenLmisResponse> responseEntity = controller.insert(supplyLine, request);

    verify(service).save(supplyLine);
    assertThat((Long) responseEntity.getBody().getData().get("supplyLineId"), CoreMatchers.is(supplyLine.getId()));
    assertThat(responseEntity.getBody().getSuccessMsg(), CoreMatchers.is("success"));
    assertThat(supplyLine.getCreatedBy(), CoreMatchers.is(1L));
    assertThat(supplyLine.getModifiedBy(), CoreMatchers.is(1L));
  }

  @Test
  public void shouldReturnErrorMessageWhenExceptionOccursOnSupplyLineInsert() {
    SupplyLine supplyLine = new SupplyLine();
    doThrow(new DataException("error")).when(service).save(supplyLine);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.insert(supplyLine, request);

    verify(service).save(supplyLine);
    verify(messageService, never()).message(anyString(), anyString());
    assertThat(responseEntity.getBody().getErrorMsg(), CoreMatchers.is("error"));
    assertThat(supplyLine.getCreatedBy(), CoreMatchers.is(1L));
    assertThat(supplyLine.getModifiedBy(), CoreMatchers.is(1L));
  }

  @Test
  public void shouldUpdateSupplyLineSuccessfully() {
    SupplyLine supplyLine = new SupplyLine(1L);
    Mockito.when(messageService.message("message.supply.line.updated.success")).thenReturn("success");

    ResponseEntity<OpenLmisResponse> responseEntity = controller.update(supplyLine, supplyLine.getId(), request);

    verify(service).save(supplyLine);
    assertThat((Long) responseEntity.getBody().getData().get("supplyLineId"), CoreMatchers.is(supplyLine.getId()));
    assertThat(responseEntity.getBody().getSuccessMsg(), CoreMatchers.is("success"));
    assertThat(supplyLine.getModifiedBy(), CoreMatchers.is(1L));
  }

  @Test
  public void shouldReturnErrorMessageWhenExceptionOccursOnSupplyLineUpdate() {
    SupplyLine supplyLine = new SupplyLine(1L);
    doThrow(new DataException("error")).when(service).save(supplyLine);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.update(supplyLine, 1L, request);

    verify(service).save(supplyLine);
    verify(messageService, never()).message(anyString(), anyString());
    assertThat(responseEntity.getBody().getErrorMsg(), CoreMatchers.is("error"));
    assertThat(supplyLine.getModifiedBy(), CoreMatchers.is(1L));
  }

  @Test
  public void shouldGetSupervisoryNodeById() {
    Long id = 1L;
    SupplyLine expectedSupplyLine = new SupplyLine();
    Mockito.when(service.getById(id)).thenReturn(expectedSupplyLine);

    SupplyLine supplyLine = controller.getById(id);

    verify(service).getById(id);
    assertThat(supplyLine, CoreMatchers.is(expectedSupplyLine));
  }

}