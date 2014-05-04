/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.sms.service;


import lombok.NoArgsConstructor;
import org.openlmis.sms.domain.SMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;

@Service
@NoArgsConstructor
public class SMSService {

    private String smsGatewayUrl;

    @Autowired
    public SMSService(@Value("${sms.gateway.url}") String smsGatewayUrl){
        this.smsGatewayUrl = smsGatewayUrl;
    }

    public Future<Boolean> send(SMS sms) {
        if (sms.getSent()) {
            return new AsyncResult<>(true);
        }

        return new AsyncResult<>(SendSMSMessage(sms));
    }

    @Async
    public Future<Boolean> sendAsync(SMS sms) {
      if (sms.getSent()) {
        return new AsyncResult<>(true);
      }

      return new AsyncResult<>(SendSMSMessage(sms));
    }

    public void ProcessSMS(@Payload List<SMS> smsList) {
        for(SMS sms : smsList){
            if(!sms.getSent()){
               SendSMSMessage(sms);
            }
        }
    }

    public Boolean SendSMSMessage (SMS sms) {
        String relayUrl = String.format("%s?message=%s&phone_number=%s",this.smsGatewayUrl, sms.getMessage().replaceAll(" ","%20"), sms.getPhoneNumber());
        try{
            URL url = new URL(relayUrl);
            url.getContent();
            return true;
        }
        catch(IOException e) {
            return false;
        }
    }
}