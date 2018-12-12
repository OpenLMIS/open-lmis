/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright ? 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * ?
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.? See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.? If not, see http://www.gnu.org/licenses. ?For additional information contact info@OpenLMIS.org.?
 */

package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.moz.ProgramDataForm;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.ProgramDataFormDTO;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestProgramDataService;
import org.openlmis.restapi.service.RestRequisitionService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openlmis.restapi.response.RestResponse.SUCCESS;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({RestResponse.class})
public class RestProgramDataControllerTest {

  Principal principal;

  @Mock
  private RestProgramDataService restProgramDataService;

  @Mock
  private RestRequisitionService restRequisitionService;

  @InjectMocks
  private RestProgramDataController restProgramDataController;

  @Before
  public void setUp() throws Exception {
    principal = mock(Principal.class);
    when(principal.getName()).thenReturn("1");
  }

  @Test
  public void shouldReturnStatusOKIfNoException() throws Exception {
    mockStatic(RestResponse.class);

    String successMsgCode = "api.program.data.save.success";
    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(SUCCESS, successMsgCode), HttpStatus.OK);
    when(RestResponse.success(successMsgCode)).thenReturn(expectedResponse);

    ProgramDataFormDTO programFormData = new ProgramDataFormDTO();
    ResponseEntity<RestResponse> response = restProgramDataController.createProgramDataForm(programFormData, principal);

    verify(restProgramDataService).createProgramDataForm(programFormData, 1L);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }

  @Test
  public void shouldReturnOKWithProgramDataFormsWhenGettingProgramDataByFacility() throws Exception {
    List<ProgramDataFormDTO> programForms = asList(new ProgramDataFormDTO(), new ProgramDataFormDTO());
    when(restProgramDataService.getProgramDataFormsByFacility(12L)).thenReturn(programForms);

    ResponseEntity<RestResponse> responseEntity = restProgramDataController.getProgramDataFormsByFacility(12L);
    verify(restProgramDataService).getProgramDataFormsByFacility(12L);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(((List<ProgramDataForm>) responseEntity.getBody().getData().get("programDataForms")).size(), is(2));
  }
}
