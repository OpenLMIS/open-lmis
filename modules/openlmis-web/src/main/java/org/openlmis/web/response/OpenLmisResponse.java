package org.openlmis.web.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenLmisResponse {
  private Object responseData;
  private String errorMsg;
  private String successMsg;

  public OpenLmisResponse(Object responseData) {
    this.responseData = responseData;
  }
}
