/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.Locale;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
    assertThat(messages.get("msg.rnr.authorized.success"), is("R&R authorized successfully!"));
  }
}
