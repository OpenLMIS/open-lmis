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


package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.SMS;
import org.openlmis.core.repository.SMSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Service
@NoArgsConstructor
public class SMSManagementService {

    @Autowired
    ConfigurationSettingService configSetting;

    @Autowired
    private SMSRepository smsRepository;
    private @Value("${sms.gateway.url}") String relayWebsite;

    public void SaveIncomingSMSMessage(String message, String phoneNumber){
        smsRepository.SaveSMSMessage("I", message,phoneNumber, new Date(),true);
    }

    public void SendSMSMessage (String message, String phoneNumber) {
        String relayUrl = String.format("%s?message=%s&phone_number=%s",relayWebsite, message.replaceAll(" ","%20"), phoneNumber);
        try{
            URL url = new URL(relayUrl);
            url.getContent();
            smsRepository.SaveSMSMessage("O",message,phoneNumber,new Date(),true);
        }
        catch(IOException e) {
            //TODO: handle this.
        }

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
            smsRepository.SaveSMSMessage("Outgoing", content, phoneNumber, new Date(),true);
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
