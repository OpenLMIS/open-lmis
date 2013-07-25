package org.openlmis.restapi.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.CHW;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestCHWService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RestResponse.class)
@Category(UnitTests.class)
public class RestCHWControllerTest {

  @Mock
  RestCHWService restCHWService;

  @InjectMocks
  RestCHWController restCHWController;

  @Test
  public void shouldCreateCHW() throws Exception {
    CHW chw = mock(CHW.class);
    mockStatic(RestResponse.class);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.success("message.success.chw.created")).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = restCHWController.createCHW(chw);

    verify(restCHWService).create(chw);
    assertThat(response, is(expectResponse));
  }

  @Test
  public void shouldUpdateCHW() throws Exception {
    CHW chw = mock(CHW.class);
    mockStatic(RestResponse.class);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.success("message.success.chw.updated")).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = restCHWController.updateCHW(chw);

    verify(restCHWService).update(chw);
    assertThat(response, is(expectResponse));
  }

}
