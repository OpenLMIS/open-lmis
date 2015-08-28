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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.openlmis.web.controller.MessagesController.MESSAGES;
@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class MessagesControllerTest {

  @Mock
  private MessageService messageService;

  @InjectMocks
  MessagesController messagesController;

  @Test
  public void shouldGetAllMessages() throws Exception {
    when(messageService.getCurrentLocale()).thenReturn(Locale.getDefault());
    ResponseEntity<OpenLmisResponse> response = messagesController.getAllMessages();
    Map<String, String> messages = (Map<String, String>)response.getBody().getData().get(MESSAGES);
    assertNotNull(messages);
  }
}
