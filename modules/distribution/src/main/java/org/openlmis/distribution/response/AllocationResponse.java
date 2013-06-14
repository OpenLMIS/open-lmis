/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.response;

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
public class AllocationResponse {
  public static final String ERROR = "error";
  public static final String SUCCESS = "success";

  private Map<String, Object> data = new HashMap<>();

  public AllocationResponse(String key, Object data) {
    this.data.put(key, data);
  }

  @JsonAnySetter
  public void addData(String key, Object data) {
    this.data.put(key, data);
  }

  public ResponseEntity<AllocationResponse> response(HttpStatus status) {
    return new ResponseEntity<>(this, status);
  }

  public static ResponseEntity<AllocationResponse> success(String successMessage) {
    return response(SUCCESS, successMessage, HttpStatus.OK, MediaType.APPLICATION_JSON_VALUE);
  }

  public static ResponseEntity<AllocationResponse> success(String successMessage, String contentType) {
    return response(SUCCESS, successMessage, HttpStatus.OK, contentType);
  }

  public static ResponseEntity<AllocationResponse> error(String errorMessage, HttpStatus statusCode) {
    return error(errorMessage, statusCode, MediaType.APPLICATION_JSON_VALUE);
  }

  public static ResponseEntity<AllocationResponse> error(String errorMessage, HttpStatus statusCode, String contentType) {
    return response(ERROR, errorMessage, statusCode, contentType);
  }

  public static ResponseEntity<AllocationResponse> error(DataException exception, HttpStatus httpStatus, String contentType) {
    return response(ERROR, exception.getOpenLmisMessage().toString(), httpStatus, contentType);
  }

  public static ResponseEntity<AllocationResponse> error(DataException exception, HttpStatus httpStatus) {
    return error(exception, httpStatus, MediaType.APPLICATION_JSON_VALUE);
  }

  public static ResponseEntity<AllocationResponse> response(String key, String message, HttpStatus statusCode, String contentType) {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", contentType);
    return new ResponseEntity<>(new AllocationResponse(key, message), headers, statusCode);
  }

  public static ResponseEntity<AllocationResponse> response(String key, Object value) {
    return new ResponseEntity<>(new AllocationResponse(key, value), HttpStatus.OK);
  }

  public static ResponseEntity<AllocationResponse> response(Map<String, String> messages, HttpStatus status) {
    return response(messages, status, MediaType.APPLICATION_JSON_VALUE);
  }

  public static ResponseEntity<AllocationResponse> response(Map<String, String> messages, HttpStatus status, String contentType) {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", contentType);
    AllocationResponse response = new AllocationResponse();
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
