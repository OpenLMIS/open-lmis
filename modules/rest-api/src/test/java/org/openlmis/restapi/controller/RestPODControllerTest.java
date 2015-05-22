/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestPODService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.response.RestResponse.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class RestPODControllerTest {

  @Mock
  RestPODService restPODService;

  @Mock
  Principal principal;

  @InjectMocks
  private RestPODController controller;

  @Before
  public void setUp() throws Exception {
    mockStatic(RestResponse.class);
  }

  @Test
  public void shouldSavePOD() throws Exception {
    OrderPOD orderPod = new OrderPOD();
    when(principal.getName()).thenReturn("2");
    doNothing().when(restPODService).updatePOD(orderPod, 2L);

    ResponseEntity<RestResponse> response = new ResponseEntity<>(new RestResponse(SUCCESS, "success"), OK);
    PowerMockito.when(success("message.success.pod.updated")).thenReturn(response);

    ResponseEntity<RestResponse> responseEntity = controller.savePOD(orderPod, "ON123", principal);

    assertThat(responseEntity.getBody().getSuccess(), is("success"));
    verify(restPODService).updatePOD(orderPod, 2L);
  }

  @Test
  public void shouldThrowErrorIfSaveUnSuccessFul() throws Exception {
    OrderPOD orderPod = new OrderPOD();

    when(principal.getName()).thenReturn("2");
    DataException dataException = new DataException("error.pod.updated");
    doThrow(dataException).when(restPODService).updatePOD(orderPod, 2L);
    ResponseEntity<RestResponse> response = new ResponseEntity<>(new RestResponse(ERROR, "error"), BAD_REQUEST);
    PowerMockito.when(error(dataException.getOpenLmisMessage(), BAD_REQUEST)).thenReturn(response);

    ResponseEntity<RestResponse> responseEntity = controller.savePOD(orderPod, "ON123", principal);

    assertThat(responseEntity.getBody().getError(), is("error"));
  }
}
