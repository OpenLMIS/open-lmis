package org.openlmis.web.controller.vaccine.demographic;

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
import org.openlmis.vaccine.dto.DemographicEstimateForm;
import org.openlmis.vaccine.dto.DemographicEstimateLineItem;
import org.openlmis.vaccine.service.demographics.FacilityDemographicEstimateService;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityDemographicEstimateControllerTest {

  @Mock
  FacilityDemographicEstimateService service;

  @InjectMocks
  FacilityDemographicEstimateController controller;

  private static final Long USER_ID = 1L;
  private MockHttpServletRequest request;
  private static final String USER = "user";

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
    DemographicEstimateForm form = new DemographicEstimateForm();
    form.setEstimateLineItems(new ArrayList<DemographicEstimateLineItem>());
    when(service.getEstimateForm(1L,2L, 2005)).thenReturn(form);

    ResponseEntity<OpenLmisResponse> response = controller.get(2005,2L,  request);

    assertThat(form, is(response.getBody().getData().get("estimates")));
    verify(service).getEstimateForm(1L,2L, 2005);
  }

  @Test
  public void shouldSave() throws Exception {
    DemographicEstimateForm form = new DemographicEstimateForm();
    form.setEstimateLineItems(new ArrayList<DemographicEstimateLineItem>());
    doNothing().when(service).save(form);

    ResponseEntity<OpenLmisResponse> response = controller.save(form, request);

    assertThat(form, is(response.getBody().getData().get("estimates")));
    verify(service).save(form);
  }
}