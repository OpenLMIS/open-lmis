/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

    @Async
    public Future<Boolean> send(SMS sms) throws IOException{
        if (sms.getSent()) {
            return new AsyncResult<>(true);
        }

        SendSMSMessage(sms);
        return new AsyncResult<>(true);
    }

    public void ProcessSMS(@Payload List<SMS> smsList) throws IOException{
        for(SMS sms : smsList){
            if(!sms.getSent()){
               this.send(sms);
            }
        }
    }

    public void SendSMSMessage (SMS sms) throws IOException {
        String relayUrl = String.format("%s?message=%s&phone_number=%s",this.smsGatewayUrl, sms.getMessage().replaceAll(" ","%20"), sms.getPhoneNumber());
        try{
            URL url = new URL(relayUrl);
            url.getContent();
        }
        catch(Exception e) {
            throw e;
        }
    }
}