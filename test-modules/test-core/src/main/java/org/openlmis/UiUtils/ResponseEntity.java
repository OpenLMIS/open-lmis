package org.openlmis.UiUtils;

import lombok.Data;
import org.apache.http.HttpStatus;

@Data
public class ResponseEntity {
  private int status;
  private String response;
}
