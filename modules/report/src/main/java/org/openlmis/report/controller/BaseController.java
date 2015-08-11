/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.controller;

import org.openlmis.core.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

  private static Logger logger = LoggerFactory.getLogger(BaseController.class);

  public static final String ACCEPT_JSON = "Accept=application/json";
  public static final String FORBIDDEN_EXCEPTION = "forbidden.exception";
  public static final String UNEXPECTED_EXCEPTION = "unexpected.exception";


  @Autowired
  MessageService messageService;

  Long loggedInUserId(HttpServletRequest request) {
    return (Long) request.getSession().getAttribute("USER_ID");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException(Exception ex) {
    logger.error("something broke with following exception... ", ex);
    if (ex instanceof AccessDeniedException) {
        //response(ERROR, errorMessage, statusCode, contentType);
      return null;//error(messageService.message(FORBIDDEN_EXCEPTION), HttpStatus.FORBIDDEN);
    }
    return null;//error(messageService.message(UNEXPECTED_EXCEPTION), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}