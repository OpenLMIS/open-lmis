/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ConfigurationSetting;
import org.openlmis.core.domain.ConfigurationSettingKey;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

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
  ConfigurationSettingService settingService;

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
    when(settingService.getConfigurationStringValue(ConfigurationSettingKey.LOGIN_SUCCESS_DEFAULT_LANDING_PAGE)).thenReturn("/public/site/index.html#/home");
    String homePageURl = homeController.homeDefault();
    assertEquals("redirect:/public/site/index.html#/home", homePageURl);
  }

  @Test
  public void shouldGetLocales() {
    mockStatic(RequestContextUtils.class);
    when(RequestContextUtils.getLocale(request)).thenReturn(Locale.getDefault());
    Set<String> localeMap = new HashSet<>();
    localeMap.add("pt");
    localeMap.add("en");
    when(messageService.getLocales()).thenReturn(localeMap);

    ResponseEntity<OpenLmisResponse> locales = homeController.getLocales(request);

    Set<String> foundLocales = (Set<String>) locales.getBody().getData().get("locales");
    assertThat(foundLocales, is(localeMap));
  }

  @Test
  public void shouldChangeLocale() {
    when(RequestContextUtils.getLocale(request)).thenReturn(Locale.getDefault());

    homeController.changeLocale(request);

    verify(messageService).setCurrentLocale(Locale.getDefault());
  }
}
