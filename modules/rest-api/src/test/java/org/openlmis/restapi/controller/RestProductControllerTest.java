package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Product;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.ProductResponse;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestProductService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.response.RestResponse.SUCCESS;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class RestProductControllerTest {
  private final static String versionCode = "86";

  @InjectMocks
  private RestProductController restProductController;

  @Mock
  private RestProductService restProductService;
  private Principal principal;

  @Before
  public void setup() {
    principal = mock(Principal.class);
    PowerMockito.when(principal.getName()).thenReturn("1");
  }

  @Test
  public void shouldCallRestKitServiceToSaveKitAndRespondWithSuccess() {
    mockStatic(RestResponse.class);
    String successMsg = "msg.kit.savesuccess";
    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(SUCCESS, successMsg), HttpStatus.OK);
    PowerMockito.when(RestResponse.success(successMsg)).thenReturn(expectedResponse);

    Product fakeKit = new Product();
    ResponseEntity<RestResponse> response = restProductController.createOrUpdateProduct(fakeKit);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(restProductService).buildAndSave(fakeKit);
  }

  @Test
  public void shouldReturnResponseWithListOfLatestProgramsWithProducts() {
    List<ProductResponse> products = new ArrayList();
    products.add(new ProductResponse());
    long date = 1234L;
    Date afterUpdatedTime = new Date(date);
    when(restProductService.getLatestProductsAfterUpdatedTime(afterUpdatedTime, versionCode, 1L)).thenReturn(products);

    ResponseEntity<RestResponse> response = restProductController.getLatestProducts(date, versionCode, principal);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(products, response.getBody().getData().get("latestProducts"));
    assertNotNull(response.getBody().getData().get("latestUpdatedTime"));
  }

  @Test
  public void shouldCallLatestProgramsWithProductsWithNullTimeWhenTimeIsNotProvided() {
    ResponseEntity<RestResponse> response = restProductController.getLatestProducts(null, "86", principal);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(restProductService).getLatestProductsAfterUpdatedTime(null, versionCode, 1L);
  }
}