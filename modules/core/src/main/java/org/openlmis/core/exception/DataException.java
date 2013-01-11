package org.openlmis.core.exception;

import lombok.Getter;
import org.openlmis.core.message.OpenLmisMessage;


public class DataException extends RuntimeException {

  @Getter
  private OpenLmisMessage openLmisMessage;


  public DataException(String message) {
    super(message);
  }

  public DataException(OpenLmisMessage openLmisMessage) {
    this.openLmisMessage = openLmisMessage;
  }

  @Deprecated
  @Override
  public String getMessage() {
    if (openLmisMessage == null) return super.getMessage();
    return openLmisMessage.toString();
  }
}
