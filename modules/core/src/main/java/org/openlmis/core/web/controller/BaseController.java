/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.web.controller;

import org.openlmis.core.service.MessageService;
import org.openlmis.core.logging.ApplicationLogger;
import org.openlmis.core.web.OpenLmisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.core.web.OpenLmisResponse.error;

/**
 * This controller has responsibility to respond with appropriate error response for an exception. Every controller should extend this class.
 */

public class BaseController {
  private static Logger logger = LoggerFactory.getLogger(ApplicationLogger.class);
  public static final String UNEXPECTED_EXCEPTION = "unexpected.exception";
  public static final String FORBIDDEN_EXCEPTION = "error.authorisation";
  public static final String ACCEPT_JSON = "Accept=application/json";
  public static final String ACCEPT_PDF = "Accept=application/pdf";
  public static final String ACCEPT_CSV = "Accept=*/*";

  @Autowired
  public MessageService messageService;

  protected Long loggedInUserId(HttpServletRequest request) {
    return (Long) request.getSession().getAttribute("USER_ID");
  }

  protected String homePageUrl() {
    return "redirect:/public/site/index.html#/home";
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<OpenLmisResponse> handleException(Exception ex) {
    logger.error("something broke with following exception... ", ex);
    if (ex instanceof AccessDeniedException) {
      return error(messageService.message(FORBIDDEN_EXCEPTION), HttpStatus.FORBIDDEN);
    }
    return error(messageService.message(UNEXPECTED_EXCEPTION), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
