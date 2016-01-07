package org.openlmis.restapi.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Product;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestProductService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.openlmis.restapi.response.RestResponse.SUCCESS;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class RestProductControllerTest {

  @InjectMocks
  private RestProductController restProductController;

  @Mock
  private RestProductService restProductService;

  @Test
  public void shouldCallRestKitServiceToSaveKitAndRespondWithSuccess() {
    mockStatic(RestResponse.class);
    String successMsg = "msg.kit.createsuccess";
    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(SUCCESS, successMsg), HttpStatus.OK);
    PowerMockito.when(RestResponse.success(successMsg)).thenReturn(expectedResponse);

    Product fakeKit = new Product();
    ResponseEntity<RestResponse> response = restProductController.createProduct(fakeKit);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(restProductService).buildAndSave(fakeKit);
  }
}