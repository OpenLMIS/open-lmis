package org.openlmis.web.controller;

import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class MessagesController {

  public static final String MESSAGES = "messages";

  @RequestMapping(value = "/messages", method = GET, headers = "Accept=application/json")
  public ResponseEntity<OpenLmisResponse> getAllMessages() {
    Map<String, String> result = new HashMap<>();
    ResourceBundle messages = ResourceBundle.getBundle("messages");
    for (String key : messages.keySet()) {
      result.put(key, messages.getString(key));
    }
    return OpenLmisResponse.response(MESSAGES, result);
  }
}
