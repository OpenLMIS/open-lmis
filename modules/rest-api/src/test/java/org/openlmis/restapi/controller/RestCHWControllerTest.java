package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.CHW;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestCHWService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

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

  Principal principal;

  @Before
  public void setUp() throws Exception {
    principal = mock(Principal.class);
    when(principal.getName()).thenReturn("vendor name");
    mockStatic(RestResponse.class);
  }

  @Test
  public void shouldCreateCHW() throws Exception {
    CHW chw = mock(CHW.class);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.success("message.success.chw.created")).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = restCHWController.createCHW(chw, principal);

    verify(restCHWService).create(chw, principal.getName());
    assertThat(response, is(expectResponse));
  }

  @Test
  public void shouldUpdateCHW() throws Exception {
    CHW chw = mock(CHW.class);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.success("message.success.chw.updated")).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = restCHWController.updateCHW(chw, principal);

    verify(restCHWService).update(chw, principal.getName());
    assertThat(response, is(expectResponse));
  }

}
