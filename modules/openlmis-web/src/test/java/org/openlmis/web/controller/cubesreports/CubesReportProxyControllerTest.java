package org.openlmis.web.controller.cubesreports;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.openlmis.db.categories.UnitTests;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class CubesReportProxyControllerTest {

    private RestTemplate restTemplate = mock(RestTemplate.class);

    @InjectMocks
    private CubesReportProxyController controller;

    @Test
    public void shouldRedirectDecodedWildCardRequestToCubesServer() throws Exception {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/cubesreports/abc");
        request.setQueryString("cut%3ddrug%40default%3a08R01Z");

        when(restTemplate.exchange("http://localhost:5555/abc?cut=drug@default:08R01Z", HttpMethod.GET, HttpEntity.EMPTY, String.class))
                .thenReturn(new ResponseEntity<String>("hello from cubes", HttpStatus.OK));

        //when
        ResponseEntity<String> responseEntity = controller.redirect(request);

        //then
        assertThat(responseEntity.getBody(), is("hello from cubes"));
    }

    @Test
    public void shouldRedirectWildCardRequestToCubesServerWithoutQueryString() throws Exception {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/cubesreports/abc");
        request.setQueryString(null);

        when(restTemplate.exchange("http://localhost:5555/abc", HttpMethod.GET, HttpEntity.EMPTY, String.class))
                .thenReturn(new ResponseEntity<String>("hello from cubes", HttpStatus.OK));

        //when
        ResponseEntity<String> responseEntity = controller.redirect(request);

        //then
        assertThat(responseEntity.getBody(), is("hello from cubes"));
    }
}