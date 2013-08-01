/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class HomeControllerTest {

  @Mock
  HttpServletRequest request;
  @Mock
  HttpSession session;

  @Mock
  MessageService messageService;

  @InjectMocks
  HomeController homeController;

  @Before
  public void setUp() {
    when(request.getSession()).thenReturn(session);
  }


  @Test
  public void shouldRedirectToHomePage() {
    String homePageURl = homeController.homeDefault();
    assertEquals("redirect:/public/pages/index.html", homePageURl);
  }

  @Test
  public void shouldGetLocales() {
    mockStatic(RequestContextUtils.class);
    when(RequestContextUtils.getLocale(request)).thenReturn(Locale.getDefault());
    Map<String, String> localeMap = new HashMap<String, String>();
    localeMap.put("pt", "Portuguese");
    localeMap.put("en", "English");
    when(messageService.getLocales()).thenReturn(localeMap);

    ResponseEntity<OpenLmisResponse> locales = homeController.getLocales(request);

    Map<String, String> foundLocales = (Map<String, String>) locales.getBody().getData().get("locales");
    assertThat(foundLocales, is(localeMap));
  }

  @Test
  public void shouldChangeLocale() {
    when(RequestContextUtils.getLocale(request)).thenReturn(Locale.getDefault());

    homeController.changeLocale(request);

    verify(messageService).setCurrentLocale(Locale.getDefault());
  }

}
