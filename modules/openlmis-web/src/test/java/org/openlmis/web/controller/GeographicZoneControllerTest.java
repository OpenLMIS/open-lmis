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
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class GeographicZoneControllerTest {

  private MockHttpServletRequest request = new MockHttpServletRequest();

  @Mock
  private GeographicZoneService service;

  @Mock
  private MessageService messageService;

  @InjectMocks
  private GeographicZoneController controller;

  @Test
  public void shouldGetGeographicZoneById() {
    GeographicZone geographicZone = new GeographicZone();
    Long geoZoneId = 1L;
    when(service.getById(geoZoneId)).thenReturn(geographicZone);

    ResponseEntity<OpenLmisResponse> response = controller.get(geoZoneId);

    assertThat((GeographicZone) response.getBody().getData().get("geoZone"), is(geographicZone));
    verify(service).getById(geoZoneId);
  }

  @Test
  public void shouldInsertGeoZone() throws Exception {
    GeographicZone geographicZone = new GeographicZone();
    geographicZone.setName("GZ 1");
    doNothing().when(service).save(geographicZone);
    when(messageService.message("message.geo.zone.created.success", geographicZone.getName())).thenReturn("created");

    ResponseEntity<OpenLmisResponse> response = controller.insert(geographicZone, request);

    assertThat((GeographicZone) response.getBody().getData().get("geoZone"), is(geographicZone));
    assertThat(response.getBody().getSuccessMsg(), is("created"));
    verify(service).save(geographicZone);
  }

  @Test
  public void shouldThrowExceptionIfInvalidGeoZoneBeingInserted() throws Exception {
    GeographicZone geographicZone = new GeographicZone();
    doThrow(new DataException("error")).when(service).save(geographicZone);

    ResponseEntity<OpenLmisResponse> errorResponse = controller.insert(geographicZone, request);

    assertThat(errorResponse.getBody().getErrorMsg(), is("error"));
    assertThat(errorResponse.getStatusCode(), is(BAD_REQUEST));
  }

  @Test
  public void shouldThrowExceptionIfInvalidGeoZoneBeingUpdated() throws Exception {
    GeographicZone geographicZone = new GeographicZone();
    doThrow(new DataException("error")).when(service).save(geographicZone);

    ResponseEntity<OpenLmisResponse> errorResponse = controller.update(geographicZone, 9L, request);

    assertThat(errorResponse.getBody().getErrorMsg(), is("error"));
    assertThat(errorResponse.getStatusCode(), is(BAD_REQUEST));
  }

  @Test
  public void shouldUpdateGeoZone() throws Exception {
    GeographicZone geographicZone = new GeographicZone();
    geographicZone.setName("GZ 1");
    doNothing().when(service).save(geographicZone);
    when(messageService.message("message.geo.zone.updated.success", geographicZone.getName())).thenReturn("updated");

    ResponseEntity<OpenLmisResponse> response = controller.update(geographicZone, 1L, request);

    assertThat((GeographicZone) response.getBody().getData().get("geoZone"), is(geographicZone));
    assertThat(geographicZone.getId(), is(1L));
    assertThat(response.getBody().getSuccessMsg(), is("updated"));
    verify(service).save(geographicZone);
  }

  @Test
  public void shouldReturnGeographicZoneBySearchParamIfLessThanLimit(){
    String searchParam = "GZ1";
    String searchLimit = "5";
    GeographicZone zone = new GeographicZone();
    List<GeographicZone> geographicZones = new ArrayList<>();
    geographicZones.add(zone);
    when(service.getGeographicZonesCountBy(searchParam)).thenReturn(4);
    when(service.getGeographicZonesByCodeOrName(searchParam)).thenReturn(geographicZones);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.getGeographicZoneByCodeOrName(searchParam, searchLimit);

    verify(service).getGeographicZonesByCodeOrName(searchParam);
    verify(service).getGeographicZonesCountBy(searchParam);
    verify(messageService, never()).message("too.many.results.found");
    assertThat((List<GeographicZone>) responseEntity.getBody().getData().get(controller.GEO_ZONES), is(geographicZones));
  }

  @Test
  public void shouldReturnMessageIfGeographicZoneCountIsMoreThanLimit(){
    String searchParam = "GZ1";
    String searchLimit = "5";
    when(service.getGeographicZonesCountBy(searchParam)).thenReturn(10);
    when(messageService.message("too.many.results.found")).thenReturn("More results");

    ResponseEntity<OpenLmisResponse> response = controller.getGeographicZoneByCodeOrName(searchParam,searchLimit);

    assertThat((String) response.getBody().getData().get("message"), is("More results"));
    verify(service).getGeographicZonesCountBy(searchParam);
    verify(messageService).message("too.many.results.found");
    verify(service, never()).getGeographicZonesByCodeOrName(searchParam);
  }
}
