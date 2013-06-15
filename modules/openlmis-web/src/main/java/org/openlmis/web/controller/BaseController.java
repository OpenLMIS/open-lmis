/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.MessageService;
import org.openlmis.web.logger.ApplicationLogger;
import org.openlmis.web.response.OpenLmisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.web.response.OpenLmisResponse.error;

@NoArgsConstructor
public class BaseController {
  private static Logger logger = LoggerFactory.getLogger(ApplicationLogger.class);
  public static final String UNEXPECTED_EXCEPTION = "unexpected.exception";
  public static final String FORBIDDEN_EXCEPTION = "forbidden.exception";
  public static final String ACCEPT_JSON = "Accept=application/json";
  public static final String ACCEPT_PDF = "Accept=application/pdf";
  public static final String ACCEPT_CSV = "Accept=application/csv";

  @Autowired
  @Setter
  MessageService messageService;

  protected Long loggedInUserId(HttpServletRequest request) {
    return (Long) request.getSession().getAttribute(USER_ID);
  }

  protected String homePageUrl() {
    return "redirect:/public/pages/index.html";
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
