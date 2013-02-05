package org.openlmis.web.controller;

import org.junit.Test;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.web.controller.MessagesController.MESSAGES;

public class MessagesControllerTest {

  @Test
  public void shouldGetAllMessages() throws Exception {
    MessagesController messagesController = new MessagesController();
    ResponseEntity<OpenLmisResponse> response = messagesController.getAllMessages();
    Map<String, String> messages = (Map<String, String>)response.getBody().getData().get(MESSAGES);
    assertThat(messages.get("rnr.authorized.success"), is("R&R authorized successfully!"));
  }
}
