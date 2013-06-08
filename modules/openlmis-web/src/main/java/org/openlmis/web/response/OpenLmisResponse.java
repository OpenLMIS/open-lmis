/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.response;

import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@NoArgsConstructor
public class OpenLmisResponse {
  public static final String ERROR = "error";
  public static final String SUCCESS = "success";
  public static ResourceBundle resourceBundle = ResourceBundle.getBundle("messages");


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
    return success(successMsgCode, MediaType.APPLICATION_JSON_VALUE);
  }

  public static ResponseEntity<OpenLmisResponse> success(OpenLmisMessage openLmisMessage) {
    return response(SUCCESS, openLmisMessage, HttpStatus.OK, MediaType.APPLICATION_JSON_VALUE);
  }

  public static ResponseEntity<OpenLmisResponse> error(String errorMsgCode, HttpStatus statusCode) {
    return error(errorMsgCode, statusCode, MediaType.APPLICATION_JSON_VALUE);
  }

  public static ResponseEntity<OpenLmisResponse> success(String successMsgCode, String contentType) {
    return response(SUCCESS, new OpenLmisMessage(successMsgCode), HttpStatus.OK, contentType);
  }

  public static ResponseEntity<OpenLmisResponse> error(String errorMsgCode, HttpStatus statusCode, String contentType) {
    return response(ERROR, new OpenLmisMessage(errorMsgCode), statusCode, contentType);
  }

  private static ResponseEntity<OpenLmisResponse> response(String key, OpenLmisMessage message, HttpStatus statusCode, String contentType) {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", contentType);
    return new ResponseEntity<>(new OpenLmisResponse(key, message.resolve(resourceBundle)), headers, statusCode);
  }

  public static ResponseEntity<OpenLmisResponse> error(DataException exception, HttpStatus httpStatus, String contentType) {
    return response(ERROR, exception.getOpenLmisMessage(), httpStatus, contentType);
  }

  public static ResponseEntity<OpenLmisResponse> error(DataException exception, HttpStatus httpStatus) {
    return error(exception, httpStatus, MediaType.APPLICATION_JSON_VALUE);
  }

  public static ResponseEntity<OpenLmisResponse> response(String key, Object value) {
    return new ResponseEntity<>(new OpenLmisResponse(key, value), HttpStatus.OK);
  }

  public static ResponseEntity<OpenLmisResponse> response(Map<String, OpenLmisMessage> messages, HttpStatus status) {
    OpenLmisResponse response = new OpenLmisResponse();
    response.setData(messages);
    return new ResponseEntity<>(response, status);
  }

  public static ResponseEntity<OpenLmisResponse> response(Map<String, OpenLmisMessage> messages, HttpStatus status, String contentType) {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", contentType);
    OpenLmisResponse response = new OpenLmisResponse();
    response.setData(messages);
    return new ResponseEntity<>(response, headers, status);
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
