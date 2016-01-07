package org.openlmis.restapi.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.builder.ProgramWithProductsBuilder;
import org.openlmis.restapi.domain.ProgramWithProducts;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestProgramsWithProductsService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.restapi.response.RestResponse.ERROR;
import static org.openlmis.restapi.response.RestResponse.SUCCESS;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class RestProgramsWithProductsControllerTest {

    @InjectMocks
    private RestProgramsWithProductsController restProgramsWithProductsController;

    @Mock
    private RestProgramsWithProductsService restProgramsWithProductsService;

    @Mock
    private MessageService messageService;

    @Test
    public void shouldReturnBadRequestIfError() {
        mockStatic(RestResponse.class);
        DataException e = new DataException("error.facility.code.invalid");
        when(restProgramsWithProductsService.getAllProgramsWithProductsByFacilityCode("F10")).thenThrow(e);
        ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(ERROR, "error.facility.code.invalid"), BAD_REQUEST);
        when(RestResponse.error(e.getOpenLmisMessage(), BAD_REQUEST)).thenReturn(expectedResponse);

        ResponseEntity<RestResponse> response = restProgramsWithProductsController.getProgramWithProductsByFacility("F10");
        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldReturnResponseWithListOfProgramsWithProducts() {
        List<ProgramWithProducts> programsWithProducts = new ArrayList();
        programsWithProducts.add(new ProgramWithProductsBuilder().build());
        when(restProgramsWithProductsService.getAllProgramsWithProductsByFacilityCode("F10")).thenReturn(programsWithProducts);

        ResponseEntity<RestResponse> response = restProgramsWithProductsController.getProgramWithProductsByFacility("F10");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(programsWithProducts, response.getBody().getData().get("programsWithProducts"));

    }

    @Test
    public void shouldReturnBadRequestIfErrorWhenRequestLatestProgramsWithProducts() {
        mockStatic(RestResponse.class);
        DataException e = new DataException("error.facility.id.invalid");
        long facilityId = 123L;
        when(restProgramsWithProductsService.getLatestProgramsWithProductsByFacilityId(facilityId, new Date(1L))).thenThrow(e);
        ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(ERROR, "error.facility.id.invalid"), BAD_REQUEST);
        when(RestResponse.error(e.getOpenLmisMessage(), BAD_REQUEST)).thenReturn(expectedResponse);

        ResponseEntity<RestResponse> response = restProgramsWithProductsController.getLatestProgramWithProductsByFacility(facilityId,1L);
        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldReturnResponseWithListOfLatestProgramsWithProducts() {
        List<ProgramWithProducts> programsWithProducts = new ArrayList();
        programsWithProducts.add(new ProgramWithProductsBuilder().build());
        long facilityId = 1L;
        long date = 1234L;
        Date afterUpdatedTime = new Date(date);
        when(restProgramsWithProductsService.getLatestProgramsWithProductsByFacilityId(facilityId, afterUpdatedTime)).thenReturn(programsWithProducts);

        ResponseEntity<RestResponse> response = restProgramsWithProductsController.getLatestProgramWithProductsByFacility(facilityId,date);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(programsWithProducts, response.getBody().getData().get("programsWithProducts"));
        assertNotNull(response.getBody().getData().get("latestUpdatedTime"));
    }

    @Test
    public void shouldCallLatestProgramsWithProductsWithNullTimeWhenTimeIsNotProvided() {
        ResponseEntity<RestResponse> response = restProgramsWithProductsController.getLatestProgramWithProductsByFacility(123L,null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restProgramsWithProductsService).getLatestProgramsWithProductsByFacilityId(123L, null);
    }
}
