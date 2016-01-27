/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.response;

import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * RestResponse encapsulates ResponseEntity, HttpStatus in order to consistently return responses.
 */

@NoArgsConstructor
@Component
public class RestResponse {
  public static final String ERROR = "error";
  public static final String SUCCESS = "success";

  private static MessageService messageService;

  @Autowired(required = true)
  public void setMessageService(MessageService messageService){
    RestResponse.messageService = messageService;
  }

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

  public static ResponseEntity<RestResponse> error(OpenLmisMessage openLmisMessage, HttpStatus statusCode) {
    return new ResponseEntity<>(new RestResponse(ERROR, messageService.message(openLmisMessage)), statusCode);
  }

  public static ResponseEntity<RestResponse> error(String errorMsgCode, HttpStatus statusCode) {
    return new ResponseEntity<>(new RestResponse(ERROR, messageService.message(new OpenLmisMessage(errorMsgCode))), statusCode);
  }

  public static ResponseEntity<RestResponse> error(DataException exception, HttpStatus httpStatus) {
    return new ResponseEntity<>(new RestResponse(ERROR, messageService.message(exception.getOpenLmisMessage().getCode())), httpStatus);
  }

  public static ResponseEntity<RestResponse> response(String key, Object value) {
    return new ResponseEntity<>(new RestResponse(key, value), HttpStatus.OK);
  }

  public static ResponseEntity<RestResponse> response(String key, Object value, HttpStatus status) {
    return new ResponseEntity<>(new RestResponse(key, value), status);
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

  @JsonIgnore
  public String getError() {
    return (String) data.get(ERROR);
  }

  @JsonIgnore
  public String getSuccess() {
    return (String) data.get(SUCCESS);
  }

}
