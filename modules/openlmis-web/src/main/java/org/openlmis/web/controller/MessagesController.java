/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.openlmis.core.service.MessageService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller handles endpoint related to get messages in the current locale.
 */

@Controller
public class MessagesController extends BaseController {

  public static final String MESSAGES = "messages";

  @Autowired
  private MessageService messageService;

  @RequestMapping(value = "/messages", method = GET, headers = "Accept=application/json")
  public ResponseEntity<OpenLmisResponse> getAllMessages() throws UnsupportedEncodingException {
    Map<String, String> result = messageService.allMessages();
    return OpenLmisResponse.response(MESSAGES, result);
  }
}
