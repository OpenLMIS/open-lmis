package org.openlmis.web.controller;

import org.junit.Test;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RnrService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RnrControllerTest {

    private static final String USER = "user";

    @Test
    public void shouldSaveWIPRnr() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
        request.setSession(session);

        RnrService rnrService = mock(RnrService.class);
        RnrController controller = new RnrController(rnrService);
        Rnr rnr = new Rnr();
        controller.saveRnr(rnr, request);
        verify(rnrService).save(rnr);
        assertThat(rnr.getModifiedBy(), is(equalTo(USER)));
    }
}
