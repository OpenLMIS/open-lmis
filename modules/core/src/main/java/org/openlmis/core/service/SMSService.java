/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */


package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.SMS;
import org.openlmis.core.repository.SMSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Service
@NoArgsConstructor
public class SMSService {

    @Autowired
    ConfigurationSettingService configSetting;

    @Autowired
    private SMSRepository smsRepository;
    //private String relayWebsite = "http://sms.tz.elmis-dev.org/rapidsms_relay";
    private String relayWebsite = "http://127.0.0.1:8000/rapidsms_relay";

    public void SaveIncomingSMSMessage(String message, String phoneNumber){
        smsRepository.SaveSMSMessage("I", message,phoneNumber, new Date());
    }

    public void SendSMSMessage (String message, String phoneNumber) throws IOException{
        String relayUrl = String.format("%s/?message=%s&phone_number=%s",relayWebsite, message.replaceAll(" ","%20"), phoneNumber);
        try{
            URL url = new URL(relayUrl);
            url.getContent();
            smsRepository.SaveSMSMessage("O",message,phoneNumber,new Date());
        }
        catch(Exception e) {
            throw e;
        }

    }

    //Saving Incoming SMS
    public  void SaveIncomingSMS(String content,String phoneNumber){
        smsRepository.saveSMSMessage("Incoming", 1, content, phoneNumber, new Date());

    }

    // sending sms
    public void sendSms(String content,String phoneNumber) throws IOException{

        String pushSmsUrl =  configSetting.getConfigurationStringValue("KANNEL_SETTINGS").toString();

        String urlString = pushSmsUrl+"&text="+content.replaceAll(" ","+")+"&to="+phoneNumber.toString();

        try {
            URL url = new URL(urlString.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = "";
            StringBuilder buffer = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                buffer = buffer.append(line).append("\n");
            }
            smsRepository.saveSMSMessage("Outgoing", 0, content, phoneNumber, new Date());
            System.out.println("Submit request= " + urlString.toString());
            System.out.println("response : "+buffer.toString());
            System.out.println("INFO : all sent disconnect.");

        } catch (Exception e){
            e.fillInStackTrace();
        }
    }

    //Get all sms
    public List<SMS>getSmsMessages(){
        return smsRepository.getAllSMS();
    }

    public List<SMS> getMessagesForMobile(String mobile){
        return smsRepository.getForMobile(mobile);
    }



}
