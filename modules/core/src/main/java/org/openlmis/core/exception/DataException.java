package org.openlmis.core.exception;

import lombok.Getter;
import org.openlmis.core.message.OpenLmisMessage;


public class DataException extends RuntimeException {

  @Getter
  private OpenLmisMessage openLmisMessage;


  public DataException(String code) {
    openLmisMessage = new OpenLmisMessage(code);
  }

  public DataException(OpenLmisMessage openLmisMessage) {
    this.openLmisMessage = openLmisMessage;
  }

  @Deprecated
  @Override
  public String getMessage() {
    return openLmisMessage.toString();
  }
}
