/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.response;

import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@NoArgsConstructor
public class RestResponse {
  public static final String ERROR = "error";
  public static final String SUCCESS = "success";

  @Autowired
  private static MessageService messageService;

  private Map<String, Object> data = new HashMap<>();

  public RestResponse(String key, Object data) {
    this.data.put(key, data);
  }

  @JsonAnySetter
  public void addData(String key, Object data) {
    this.data.put(key, data);
  }

  public ResponseEntity<RestResponse> response(HttpStatus status) {
    return new ResponseEntity<>(this, status);
  }

  public static ResponseEntity<RestResponse> success(String successMsgCode) {
    return new ResponseEntity<>(new RestResponse(SUCCESS, messageService.message(new OpenLmisMessage(successMsgCode))), HttpStatus.OK);
  }

  public static ResponseEntity<RestResponse> success(OpenLmisMessage openLmisMessage) {
    return new ResponseEntity<>(new RestResponse(SUCCESS, messageService.message(openLmisMessage)), HttpStatus.OK);
  }

  public static ResponseEntity<RestResponse> error(String errorMsgCode, HttpStatus statusCode) {
    return new ResponseEntity<>(new RestResponse(ERROR, messageService.message(new OpenLmisMessage(errorMsgCode))), statusCode);
  }

  public static ResponseEntity<RestResponse> error(DataException exception, HttpStatus httpStatus) {
    return new ResponseEntity<>(new RestResponse(ERROR, messageService.message(exception.getOpenLmisMessage())), httpStatus);
  }

  public static ResponseEntity<RestResponse> response(String key, Object value) {
    return new ResponseEntity<>(new RestResponse(key, value), HttpStatus.OK);
  }

  public static ResponseEntity<RestResponse> response(Map<String, OpenLmisMessage> messages, HttpStatus status) {
    RestResponse response = new RestResponse();
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
      addData(key, messageService.message(errors.get(key)));
    }
  }

}
