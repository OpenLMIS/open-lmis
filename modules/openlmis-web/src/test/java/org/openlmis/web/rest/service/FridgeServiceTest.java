package org.openlmis.web.rest.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.rest.RestClient;
import org.openlmis.web.rest.model.ColdTraceData;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({FridgeService.class})
public class FridgeServiceTest {
    private static final String USER = "user";
    private static final String PASSWORD = "pass";
    private static final String URL = "http://localhost:3000/api/v1/fridges/";

    @Mock
    private RestClient restClient;

    @Mock
    private ColdTraceData coldTraceData;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    private FridgeService service;

    @Before
    public void setUp() throws Exception {
        whenNew(RestClient.class).withArguments(USER, PASSWORD).thenReturn(restClient);

        service = new FridgeService();
        service.setUser(USER);
        service.setPwd(PASSWORD);
        service.setUrl(URL);

        service.init();
    }

    @Test
    public void shouldRetrieveFridges() throws Exception {
        doReturn(coldTraceData).when(restClient).getForObject(anyString(), eq(ColdTraceData.class), anyString());
        ColdTraceData fridges = service.getFridges("test-dz-code");

        assertThat(fridges, is(notNullValue()));
        assertThat(fridges, is(equalTo(coldTraceData)));

        verify(restClient).getForObject(stringCaptor.capture(), eq(ColdTraceData.class), eq("test-dz-code"));

        assertThat(stringCaptor.getValue(), is(equalTo(URL + "?delivery_zone={deliveryZoneCode}")));
    }

    @Test
    public void shouldReturnNullIfThereWillBeProblemWithRetrievingFridges() throws Exception {
        doThrow(IOException.class).when(restClient).getForObject(anyString(), eq(ColdTraceData.class), anyString());
        ColdTraceData fridges = service.getFridges("test-dz-code");

        assertThat(fridges, is(nullValue()));
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotCatchRuntimeException() throws Exception {
        doThrow(RuntimeException.class).when(restClient).getForObject(anyString(), eq(ColdTraceData.class), anyString());
        service.getFridges("test-dz-code");
    }

}
