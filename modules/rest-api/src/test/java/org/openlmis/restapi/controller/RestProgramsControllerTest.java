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
import org.openlmis.restapi.service.RestProgramsService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class RestProgramsControllerTest {

  @Mock
  private RestProgramsService restProgramsService;

  @InjectMocks
  private RestProgramsController restProgramsController;

  private Principal principal;

  @Before
  public void setup() {
    principal = mock(Principal.class);
    when(principal.getName()).thenReturn("1");
  }

  @Test
  public void shouldReturnSuccessIfNoExceptionThrown() {
    List<String> programCodes = asList("P1", "P2");
    mockStatic(RestResponse.class);
    when(RestResponse.success(anyString())).thenReturn(new ResponseEntity<RestResponse>(HttpStatus.OK));

    ResponseEntity response = restProgramsController.associatePrograms(1L, programCodes);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }
}