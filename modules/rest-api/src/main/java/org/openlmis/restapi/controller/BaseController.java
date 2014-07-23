/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.controller;

import org.openlmis.core.exception.DataException;
import org.openlmis.restapi.response.RestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.Principal;

import static java.lang.Long.valueOf;
import static org.openlmis.restapi.response.RestResponse.error;
import static org.springframework.http.HttpStatus.*;

/**
 * Controller which is extended by all controllers handling public API requests. Also contains method for handling
 * any exception that is thrown by its child controller, effectively returning error response.
 */

public class BaseController {
  public static final String ACCEPT_JSON = "Accept=application/json";
  public static final String UNEXPECTED_EXCEPTION = "unexpected.exception";
  public static final String FORBIDDEN_EXCEPTION = "error.authorisation";

  @ExceptionHandler(Exception.class)
  public ResponseEntity<RestResponse> handleException(Exception ex) {
    if (ex instanceof AccessDeniedException) {
      return error(FORBIDDEN_EXCEPTION, FORBIDDEN);
    }
    if (ex instanceof MissingServletRequestParameterException || ex instanceof HttpMessageNotReadableException || ex instanceof DataException) {
      return error(ex.getMessage(), BAD_REQUEST);
    }
    return error(UNEXPECTED_EXCEPTION, INTERNAL_SERVER_ERROR);
  }

  public Long loggedInUserId(Principal principal) {
    return valueOf(principal.getName());
  }
}
