/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
/**
 * Created with IntelliJ IDEA.
 * User: Messay Yohannes <deliasmes@gmail.com>
 * To change this template use File | Settings | File Templates.
 */
package org.openlmis.odkapi.service;

import lombok.NoArgsConstructor;
import org.openlmis.odkapi.domain.ODKAccount;
import org.openlmis.odkapi.domain.ODKSubmission;
import org.openlmis.odkapi.domain.ODKSubmissionData;
import org.openlmis.odkapi.repository.ODKAccountRepository;
import org.openlmis.odkapi.repository.ODKSubmissionDataRepository;
import org.openlmis.odkapi.repository.ODKSubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.domain.Facility;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import org.apache.commons.io.IOUtils;

import org.openlmis.odkapi.parser.ODKSubmissionSAXHandler;

@Service
@NoArgsConstructor
public class ODKSubmissionService {

    @Autowired
    private ODKAccountRepository odkAccountRepository;
    @Autowired
    private ODKSubmissionRepository odkSubmissionRepository;

    @Autowired
    private ODKSubmissionDataRepository odkSubmissionDataRepository;

    @Autowired
    private FacilityService facilityService;
    private String currentDateTime;
    private SimpleDateFormat format;
    private Facility facility;
    private ODKAccount odkAccount;
    private List<ODKSubmissionData> listOfODKSubmissionData;
    private ODKSubmissionData odkSubmissionData;
    private ODKSubmission odkSubmission;
    private HashMap<Long,ArrayList<String>> associatedFacilityPictures;
    Long tempFacilityId;
    ArrayList<String> tempPictures = new ArrayList<String>();

    private Map<String, MultipartFile> facilityPictures;

    private static Logger logger = LoggerFactory.getLogger(ODKSubmissionService.class);



    private void insertODKSubmission(ODKSubmission odkSubmission)
    {
        odkSubmissionRepository.insert(odkSubmission);
    }

    private void insertODKSubmissionData(ODKSubmissionData odkSubmissionData)
    {
        odkSubmissionDataRepository.insert(odkSubmissionData);
    }


    public boolean getODKSubmissionFiles(Set submissionsFilesSet)
    {
        facilityPictures = new HashMap<String, MultipartFile>();
        Iterator iterator = submissionsFilesSet.iterator();
        MultipartFile XMLSubmissionFile = null;
        while(iterator.hasNext())
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            String fileName = (String)entry.getKey();
            if (fileName.equals("xml_submission_file"))
            {
                XMLSubmissionFile = (MultipartFile)entry.getValue();

            }
            else
            {
                facilityPictures.put(fileName, (MultipartFile)entry.getValue());
            }
        }

        return getODKSubmissionData(XMLSubmissionFile);

    }

    public boolean getODKSubmissionData(MultipartFile xmlSubmissionFile)
    {

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        ODKSubmissionSAXHandler handler;
        handler = new ODKSubmissionSAXHandler();

        try
        {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(xmlSubmissionFile.getInputStream(), handler);

        }
        catch(IOException | ParserConfigurationException | SAXException exception )
        {
            // exception.printStackTrace();

        }

        odkSubmission = handler.getOdkSubmission();
        odkAccount = handler.getOdkAccount();
        ODKAccount tempAccount = odkAccountRepository.getODKAccountByDeviceId(odkAccount.getDeviceId());
        odkSubmission.setOdkAccountId(tempAccount.getId());
        odkSubmission.setActive(true);
        this.insertODKSubmission(odkSubmission);

        listOfODKSubmissionData = handler.getListOfODKSubmissionData();
        associatedFacilityPictures = handler.getFacilityPictures();
        boolean  picturesAssigned = assignFacilityPictures();
        boolean  facilityDataSaved = saveFacilityData();
        saveODKSubmissionData();

        return ( picturesAssigned && facilityDataSaved );
    }


    public boolean assignFacilityPictures()
    {

        Iterator iterator = associatedFacilityPictures.entrySet().iterator();
        ArrayList<String> tempFileNames;
        MultipartFile tempPicture;
        InputStream imageInputStream;
        while (iterator.hasNext())
        {
            Map.Entry associatedPairs= (Map.Entry)iterator.next();
            for (ODKSubmissionData tempData : listOfODKSubmissionData)
            {
               if (tempData.getFacilityId().equals((Long)associatedPairs.getKey()))
               {
                   tempFileNames = (ArrayList<String>) associatedPairs.getValue();
                   for (String fileName:tempFileNames)
                   {
                       tempPicture = facilityPictures.get(fileName);
                       try
                       {
                           imageInputStream = tempPicture.getInputStream();
                           byte[] bytes = IOUtils.toByteArray(imageInputStream);
                           tempData.getFacilityPictures().add(bytes);
                       }
                       catch(IOException e)
                       {
                           logger.error("Facility picture not found.");
                       }


                   }
               }
            }

        }


     return true;
    }

    public void saveODKSubmissionData()
    {
       for(ODKSubmissionData tempData : this.listOfODKSubmissionData)
        {
            tempData.setFirstPicture(tempData.getFacilityPictures().get(0));
            tempData.setSecondPicture(tempData.getFacilityPictures().get(1));
            tempData.setThirdPicture(tempData.getFacilityPictures().get(2));
            tempData.setFourthPicture(tempData.getFacilityPictures().get(3));
            tempData.setFifthPicture(tempData.getFacilityPictures().get(4));
            tempData.setODKSubmissionId(odkSubmissionRepository.getLastSubmissionId());
            insertODKSubmissionData(tempData);
        }
    }


    public boolean saveFacilityData()
    {
        this.currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (ODKSubmissionData ODKfacilitySubmissionData : listOfODKSubmissionData)
        {
            // To Do Add Exception handling if facility is not found
            this.facility = facilityService.getById(ODKfacilitySubmissionData.getFacilityId());
            // To Do Add GPS Accuracy attribute to the facility model so that before updating the
            // existing GPS information the accuracy of the existing GPS info is checked.
            this.facility.setLatitude(ODKfacilitySubmissionData.getGPSLatitude());
            this.facility.setLongitude(ODKfacilitySubmissionData.getGPSLongitude());
            this.facility.setAltitude(ODKfacilitySubmissionData.getGPSAltitude());
            this.facility.setModifiedDate(new Date());
            this.facility.setComment(this.facility.getComment() + "\n" + "GPS location captured on " + currentDateTime);
            facilityService.update(this.facility);

        }

        return true;
    }


}
