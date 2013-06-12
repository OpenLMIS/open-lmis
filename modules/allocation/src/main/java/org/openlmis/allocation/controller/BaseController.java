/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.allocation.controller;

import org.openlmis.allocation.response.AllocationResponse;
import org.openlmis.allocation.service.AllocationPermissionService;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.allocation.response.AllocationResponse.error;

public class BaseController {

  private static Logger logger = LoggerFactory.getLogger(BaseController.class);

  public static final String ACCEPT_JSON = "Accept=application/json";
  public static final String FORBIDDEN_EXCEPTION = "forbidden.exception";
  public static final String UNEXPECTED_EXCEPTION = "unexpected.exception";


  @Autowired
  AllocationPermissionService permissionService;
  @Autowired
  MessageService messageService;

  Long loggedInUserId(HttpServletRequest request) {
    return (Long) request.getSession().getAttribute(UserAuthenticationSuccessHandler.USER_ID);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<AllocationResponse> handleException(Exception ex) {
    logger.error("something broke with following exception... ", ex);
    if (ex instanceof AccessDeniedException) {
      return error(messageService.message(FORBIDDEN_EXCEPTION), HttpStatus.FORBIDDEN);
    }
    return error(messageService.message(UNEXPECTED_EXCEPTION), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}