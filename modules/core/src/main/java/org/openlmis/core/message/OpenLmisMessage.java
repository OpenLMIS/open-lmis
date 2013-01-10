package org.openlmis.core.message;

import lombok.Data;

@Data
public class OpenLmisMessage {

  private String code;


  public OpenLmisMessage(String code) {
    this.code = code;
  }
}
