package org.openlmis.web.response;

import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class OpenLmisResponse {
  public static final String ERROR = "error";
  public static final String SUCCESS = "success";

  public static final String CURRENCY = "currency";
  public static final String ROLE = "role";
  public static final String ROLES = "roles";
  public static final String RIGHTS = "rights";

  private Map<String, Object> data = new HashMap<>();

  public OpenLmisResponse(String key, Object data) {
    setData(key, data);
  }

  public static ResponseEntity<OpenLmisResponse> success(String successMsg) {
    return new ResponseEntity<>(new OpenLmisResponse(SUCCESS, successMsg), HttpStatus.OK);
  }

  public static ResponseEntity<OpenLmisResponse> error(String errorMsg, HttpStatus statusCode) {
    return new ResponseEntity<>(new OpenLmisResponse(ERROR, errorMsg), statusCode);
  }

  public static ResponseEntity<OpenLmisResponse> response(String key, Object value) {
    return new ResponseEntity<>(new OpenLmisResponse(key, value), HttpStatus.OK);
  }

  @JsonAnyGetter
  @SuppressWarnings("unused")
  public Map<String, Object> getData() {
    return data;
  }

  @JsonAnySetter
  public void setData(String key, Object data) {
    this.data.put(key, data);
  }

  @JsonIgnore
  public String getErrorMsg() {
    return (String) data.get(ERROR);
  }

  @JsonIgnore
  public String getSuccessMsg() {
      return (String) data.get(SUCCESS);
    }
}
