package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.openlmis.web.rest.model.ColdTraceData;
import org.openlmis.web.rest.service.FridgeService;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doReturn;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class FridgeStatusControllerTest {

    @Mock
    private FridgeService fridgeService;

    @Mock
    private ColdTraceData coldTraceData;

    @InjectMocks
    private FridgeStatusController controller;

    @Test
    public void shouldGetFridges() throws Exception {
        doReturn(coldTraceData).when(fridgeService).getFridges(anyLong());

        ResponseEntity<OpenLmisResponse> response = controller.getFridges(10L);
        OpenLmisResponse body = response.getBody();

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(body.getData(), is(notNullValue()));
        assertThat((ColdTraceData) body.getData().get("coldTraceData"), is(coldTraceData));
    }

}
