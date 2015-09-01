/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.web;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * This class wraps the ResponseEntity, in order to consistently return response containing either data/error and status code
 */
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
    return response(SUCCESS, successMessage, OK, APPLICATION_JSON_VALUE);
  }

  public ResponseEntity<OpenLmisResponse> successEntity(String successMessage) {
    addData(SUCCESS, successMessage);
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", APPLICATION_JSON_VALUE);
    return new ResponseEntity<>(this, headers, OK);
  }

  public static ResponseEntity<OpenLmisResponse> success(String successMessage, String contentType) {
    return response(SUCCESS, successMessage, OK, contentType);
  }


  public static ResponseEntity<OpenLmisResponse> error(String errorMessage, HttpStatus statusCode) {
    return error(errorMessage, statusCode, APPLICATION_JSON_VALUE);
  }

  public ResponseEntity<OpenLmisResponse> errorEntity(DataException exception, HttpStatus httpStatus) {
    return errorEntity(exception.getOpenLmisMessage().toString(), httpStatus);
  }

  public ResponseEntity<OpenLmisResponse> errorEntity(String errorMessage, HttpStatus statusCode) {
    addData(ERROR, errorMessage);
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", APPLICATION_JSON_VALUE);
    return new ResponseEntity<>(this, headers, statusCode);
  }

  public static ResponseEntity<OpenLmisResponse> error(String errorMessage, HttpStatus statusCode, String contentType) {
    return response(ERROR, errorMessage, statusCode, contentType);
  }

  public static ResponseEntity<OpenLmisResponse> error(DataException exception, HttpStatus httpStatus, String contentType) {
    return response(ERROR, exception.getOpenLmisMessage().toString(), httpStatus, contentType);
  }

  public static ResponseEntity<OpenLmisResponse> error(DataException exception, HttpStatus httpStatus) {
    return error(exception, httpStatus, APPLICATION_JSON_VALUE);
  }

  public static ResponseEntity<OpenLmisResponse> response(String key, String message, HttpStatus statusCode, String contentType) {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", contentType);
    return new ResponseEntity<>(new OpenLmisResponse(key, message), headers, statusCode);
  }

  public static ResponseEntity<Object> response(Object value) {
    return new ResponseEntity<>(value, OK);
  }

  public static ResponseEntity<OpenLmisResponse> response(String key, Object value) {
    return new ResponseEntity<>(new OpenLmisResponse(key, value), OK);
  }

  public static ResponseEntity<OpenLmisResponse> response(String key, Object value, HttpStatus status) {
    return new ResponseEntity<>(new OpenLmisResponse(key, value), status);
  }

  public static ResponseEntity<OpenLmisResponse> response(Map<String, String> messages, HttpStatus status) {
    return response(messages, status, APPLICATION_JSON_VALUE);
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