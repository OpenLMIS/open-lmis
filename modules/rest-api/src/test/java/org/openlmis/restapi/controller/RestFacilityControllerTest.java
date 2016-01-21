/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.restapi.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.dto.FacilityFeedDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestFacilityService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class RestFacilityControllerTest {

  @Mock
  RestFacilityService restFacilityService;

  @Mock
  MessageService messageService;

  @InjectMocks
  private RestFacilityController restFacilityController;

  @Test
  public void shouldGetFacilityByCode() throws Exception {
    String facilityCode = "F11";
    FacilityFeedDTO facilityFeedDTO = new FacilityFeedDTO();
    Mockito.when(restFacilityService.getFacilityByCode(facilityCode)).thenReturn(facilityFeedDTO);

    ResponseEntity<RestResponse> response = restFacilityController.getFacilityByCode(facilityCode);

    assertThat((FacilityFeedDTO) response.getBody().getData().get("facility"), is(facilityFeedDTO));
    verify(restFacilityService).getFacilityByCode(facilityCode);
  }

  @Test
  public void shouldGiveErrorIfFacilityCodeInvalid() throws Exception {
    mockStatic(RestResponse.class);
    String facilityCode = "F11";
    OpenLmisMessage openLmisMessage = new OpenLmisMessage("error code");
    ResponseEntity<RestResponse> errorResponse = new ResponseEntity<>(BAD_REQUEST);
    when(RestResponse.error(openLmisMessage, BAD_REQUEST)).thenReturn(errorResponse);

    doThrow(new DataException(openLmisMessage)).when(restFacilityService).getFacilityByCode(facilityCode);

    ResponseEntity<RestResponse> response = restFacilityController.getFacilityByCode(facilityCode);

    assertThat(response, is(errorResponse));
  }
}
