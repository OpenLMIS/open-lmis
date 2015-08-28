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
package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.SMSManagementService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletRequest;

import static org.openlmis.core.web.OpenLmisResponse.error;
import static org.openlmis.core.web.OpenLmisResponse.success;


@Controller
@NoArgsConstructor
public class SMSController extends BaseController {

    @Autowired
    private SMSManagementService smsService;

    @RequestMapping(value = "/public/sms", method = RequestMethod.GET)
    public void IncomingSMS(@RequestParam(value = "message") String message, @RequestParam(value="phone_no") String phoneNumber){
        smsService.SaveIncomingSMSMessage(message,phoneNumber);
    }

    @RequestMapping(value = "/sms/setDetails",method = RequestMethod.GET,headers = ACCEPT_JSON)
    public void getParameterForSendingSms(@RequestParam( "content") String message,@RequestParam("mobile") String phoneNumber){
        try {
            smsService.sendSms(message,phoneNumber);

        } catch (Exception e){
            System.out.print(e.fillInStackTrace());
        }
    }

    @RequestMapping(value = "/getSMS",method = RequestMethod.GET)
    public void IncomingMessage(@RequestParam( "content") String message,@RequestParam("mobile") String phoneNumber){
        smsService.SaveIncomingSMSMessage(message,phoneNumber);
    }

    @RequestMapping(value = "/sms/MessageList", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAll(HttpServletRequest request) {
        return OpenLmisResponse.response("sms", smsService.getSmsMessages());
    }

    @RequestMapping(value = "/sms/MessagesForMobile", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getMessagesForMobilePhone(@RequestParam("mobile") String mobile) {
        return OpenLmisResponse.response("sms", smsService.getMessagesForMobile(mobile));
    }

}
