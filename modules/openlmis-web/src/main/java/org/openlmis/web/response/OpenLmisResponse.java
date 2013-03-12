package org.openlmis.web.response;

import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@NoArgsConstructor
public class OpenLmisResponse {
  public static final String ERROR = "error";
  public static final String SUCCESS = "success";
  private static ResourceBundle resourceBundle = ResourceBundle.getBundle("messages");


  private Map<String, Object> data = new HashMap<>();

  public OpenLmisResponse(String key, Object data) {
    this.data.put(key, data);
  }

  @JsonAnySetter
  public void addData(String key, Object data) {
    this.data.put(key, data);
  }

  public ResponseEntity<OpenLmisResponse> response(HttpStatus status) {
    return new ResponseEntity<>(this, status);
  }

  public static ResponseEntity<OpenLmisResponse> success(String successMsgCode) {
    return new ResponseEntity<>(new OpenLmisResponse(SUCCESS, new OpenLmisMessage(successMsgCode).resolve(resourceBundle)), HttpStatus.OK);
  }

  public static ResponseEntity<OpenLmisResponse> success(OpenLmisMessage openLmisMessage) {
    return new ResponseEntity<>(new OpenLmisResponse(SUCCESS, openLmisMessage.resolve(resourceBundle)), HttpStatus.OK);
  }

  public static ResponseEntity<OpenLmisResponse> error(String errorMsgCode, HttpStatus statusCode) {
    return new ResponseEntity<>(new OpenLmisResponse(ERROR, new OpenLmisMessage(errorMsgCode).resolve(resourceBundle)), statusCode);
  }

  public static ResponseEntity<OpenLmisResponse> error(DataException exception, HttpStatus httpStatus) {
    return new ResponseEntity<>(new OpenLmisResponse(ERROR, exception.getOpenLmisMessage().resolve(resourceBundle)), httpStatus);
  }

  public static ResponseEntity<OpenLmisResponse> response(String key, Object value) {
    return new ResponseEntity<>(new OpenLmisResponse(key, value), HttpStatus.OK);
  }

  public static ResponseEntity<OpenLmisResponse> response(Map<String, OpenLmisMessage> messages, HttpStatus status) {
    OpenLmisResponse response = new OpenLmisResponse();
    response.setData(messages);
    return new ResponseEntity<>(response, status);
  }

  @JsonAnyGetter
  @SuppressWarnings("unused")
  public Map<String, Object> getData() {
    return data;
  }

  private void setData(Map<String, OpenLmisMessage> errors) {
    for (String key : errors.keySet()) {
      addData(key, errors.get(key).resolve(resourceBundle));
    }
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
