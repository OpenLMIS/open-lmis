/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.web.controller.BaseController.UNEXPECTED_EXCEPTION;

@Category(UnitTests.class)
public class BaseControllerTest {

  @Mock
  MessageService messageService;

  @Mock
  ConfigurationSettingService settingService;

  @InjectMocks
  BaseController baseController;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }

  @Test
  public void shouldResolveUnhandledException() throws Exception {
    when(messageService.message(UNEXPECTED_EXCEPTION)).thenReturn("Oops, something has gone wrong. Please try again later");
    final ResponseEntity<OpenLmisResponse> response = baseController.handleException(new Exception());
    final OpenLmisResponse body = response.getBody();
    assertThat(body.getErrorMsg(), is("Oops, something has gone wrong. Please try again later"));
  }
}
