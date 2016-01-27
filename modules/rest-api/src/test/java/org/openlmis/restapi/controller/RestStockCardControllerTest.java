package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestStockCardService;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.dto.StockEvent;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.response.RestResponse.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({RestResponse.class})
public class RestStockCardControllerTest {

    @Mock
    RestStockCardService restStockCardService;

    @InjectMocks
    RestStockCardController restStockCardController;

    Principal principal;
    private StockCard stockCard;
    private List<StockEvent> stockEventList;
    private Long facilityId;
    private long userId;

    @Before
    public void setUp() throws Exception {
        principal = mock(Principal.class);
        when(principal.getName()).thenReturn("123");
    }

    @Test
    public void shouldReturnStatusOKIfNoException() throws Exception {
        setupStockData();
        mockStatic(RestResponse.class);

        String successMsg = "msg.stockmanagement.adjuststocksuccess";
        ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(SUCCESS, successMsg), HttpStatus.OK);
        when(RestResponse.success(successMsg)).thenReturn(expectedResponse);

        ResponseEntity<RestResponse> response = restStockCardController.adjustStock(facilityId, stockEventList, principal);

        verify(restStockCardService).adjustStock(facilityId, stockEventList, userId);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getSuccess(), is(successMsg));
    }

    @Test
    public void shouldReturnStatusBadRequestIfDataException() throws Exception {
        setupStockData();
        mockStatic(RestResponse.class);

        DataException dataException = new DataException("invalid data");
        doThrow(dataException).when(restStockCardService).adjustStock(facilityId, stockEventList, userId);

        ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(ERROR, "invalid data"), HttpStatus.BAD_REQUEST);
        when(error(dataException.getOpenLmisMessage(), HttpStatus.BAD_REQUEST)).thenReturn(expectedResponse);

        ResponseEntity<RestResponse> response = restStockCardController.adjustStock(facilityId, stockEventList, principal);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat((String) response.getBody().getData().get(ERROR), is("invalid data"));
    }


    private void setupStockData() {
        stockCard = new StockCard();
        stockCard.setId(123L);
        stockEventList = new ArrayList<>();
        facilityId = 100L;
        userId = 123L;
    }
}