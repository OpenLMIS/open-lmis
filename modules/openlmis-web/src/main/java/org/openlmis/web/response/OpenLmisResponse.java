/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.response;

import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.openlmis.core.exception.DataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class OpenLmisResponse {
  public static final String ERROR = "error";
  public static final String SUCCESS = "success";

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

  public static ResponseEntity<OpenLmisResponse> success(String successMessage) {
    return response(SUCCESS, successMessage, HttpStatus.OK, MediaType.APPLICATION_JSON_VALUE);
  }

  public static ResponseEntity<OpenLmisResponse> success(String successMessage, String contentType) {
    return response(SUCCESS, successMessage, HttpStatus.OK, contentType);
  }

  public static ResponseEntity<OpenLmisResponse> error(String errorMessage, HttpStatus statusCode) {
    return error(errorMessage, statusCode, MediaType.APPLICATION_JSON_VALUE);
  }

  public static ResponseEntity<OpenLmisResponse> error(String errorMessage, HttpStatus statusCode, String contentType) {
    return response(ERROR, errorMessage, statusCode, contentType);
  }

  public static ResponseEntity<OpenLmisResponse> error(DataException exception, HttpStatus httpStatus, String contentType) {
    return response(ERROR, exception.getOpenLmisMessage().toString(), httpStatus, contentType);
  }

  public static ResponseEntity<OpenLmisResponse> error(DataException exception, HttpStatus httpStatus) {
    return error(exception, httpStatus, MediaType.APPLICATION_JSON_VALUE);
  }

  public static ResponseEntity<OpenLmisResponse> response(String key, String message, HttpStatus statusCode, String contentType) {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", contentType);
    return new ResponseEntity<>(new OpenLmisResponse(key, message), headers, statusCode);
  }

  public static ResponseEntity<OpenLmisResponse> response(String key, Object value) {
    return new ResponseEntity<>(new OpenLmisResponse(key, value), HttpStatus.OK);
  }

  public static ResponseEntity<OpenLmisResponse> response(Map<String, String> messages, HttpStatus status) {
    return response(messages, status, MediaType.APPLICATION_JSON_VALUE);
  }

  public static ResponseEntity<OpenLmisResponse> response(Map<String, String> messages, HttpStatus status, String contentType) {
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

  private void setData(Map<String, String> errors) {
    for (String key : errors.keySet()) {
      addData(key, errors.get(key));
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
