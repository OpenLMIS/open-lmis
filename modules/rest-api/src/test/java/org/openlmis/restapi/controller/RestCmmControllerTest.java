package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestCmmService;
import org.openlmis.stockmanagement.domain.CMMEntry;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.openlmis.restapi.response.RestResponse.SUCCESS;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class RestCmmControllerTest {

  @Mock
  private RestCmmService restCmmService;

  @InjectMocks
  private RestCmmController cmmController;

  @Test
  public void shouldReturnOKWhenSuccessfullyUpdatedCMMs() throws Exception {

    Principal principal = mock(Principal.class);
    when(principal.getName()).thenReturn("123");

    CMMEntry cmmEntry = new CMMEntry();
    mockStatic(RestResponse.class);
    String successMsg = "msg.cmm.savesuccess";
    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(SUCCESS, successMsg), HttpStatus.OK);
    when(RestResponse.success(successMsg)).thenReturn(expectedResponse);

    ResponseEntity<RestResponse> response = cmmController.updateCMMsForFacility(asList(cmmEntry), 1L, principal);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

}