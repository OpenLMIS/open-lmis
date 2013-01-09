package org.openlmis.rnr.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Message {

  private String code;

  Map<String, String> allMessages = new HashMap<>();
  public Message(String code) {
    this.code = code;
  }

  @Override
  public String toString(){
    return allMessages.get(code);
  }
}
