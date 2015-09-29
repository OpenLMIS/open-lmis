package org.openlmis.web.controller.demographic;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.demographics.dto.EstimateForm;
import org.openlmis.demographics.dto.EstimateFormLineItem;
import org.openlmis.demographics.service.AnnualDistrictDemographicEstimateService;
import org.openlmis.web.controller.demographics.DistrictEstimateController;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistrictEstimateEntryControllerTest {

    private static final Long USER_ID = 1L;
    private static final String USER = "user";
    @Mock
    AnnualDistrictDemographicEstimateService service;
    @InjectMocks
    DistrictEstimateController controller;
    private MockHttpServletRequest request;

    @Before
    public void setUp() throws Exception {
        request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
        session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

        request.setSession(session);
    }

    @Test
    public void shouldGet() throws Exception {
        EstimateForm form = new EstimateForm();
        form.setEstimateLineItems(new ArrayList<EstimateFormLineItem>());
        when(service.getEstimateForm(2005, 1L, 1L)).thenReturn(form);

        ResponseEntity<OpenLmisResponse> response = controller.get(2005, 1L, request);

        assertThat(form, is(response.getBody().getData().get("estimates")));
        verify(service).getEstimateForm(2005, 1L, 1L);
    }

    @Test
    public void shouldSave() throws Exception {
        EstimateForm form = new EstimateForm();
        form.setEstimateLineItems(new ArrayList<EstimateFormLineItem>());
        doNothing().when(service).save(form, 1L);

        ResponseEntity<OpenLmisResponse> response = controller.save(form, request);

        assertThat(form, is(response.getBody().getData().get("estimates")));
        verify(service).save(form, 1L);
    }
}