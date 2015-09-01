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
package org.openlmis.odkapi.service;

import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProductService;
import org.openlmis.odkapi.domain.*;
import org.openlmis.odkapi.exception.*;
import org.openlmis.odkapi.parser.*;
import org.openlmis.odkapi.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@Service
@NoArgsConstructor
public class ODKSubmissionService {

    @Autowired
    private ODKAccountService odkAccountService;
    @Autowired
    private ODKSubmissionRepository odkSubmissionRepository;

    @Autowired
    private ODKSubmissionDataRepository odkSubmissionDataRepository;

    @Autowired
    private ODKXFormService odkxFormService;

    @Autowired
    private ODKProofOfDeliverySubmissionDataRepository odkProofOfDeliverySubmissionDataRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ODKRandRSubmissionRepository odkRandRSubmissionRepository;

    @Autowired
    private ODKLMISSurveySubmissionRepository odklmisSurveySubmissionRepository;

    @Autowired
    private ODKStorageSurveySubmissionRepository odkStorageSurveySubmissionRepository;

    @Autowired
    ServletContext servletContext;

    @Autowired
    private FacilityService facilityService;
    private String currentDateTime;
    private SimpleDateFormat format;
    private Facility facility;
    private ODKAccount odkAccount;
    private List<ODKSubmissionData> listOfODKSubmissionData;
    private List<ODKStockStatusSubmission> listODKStockStatusSubmissions;
    private ODKProofOfDeliverySubmissionData odkProofOfDeliverySubmissionData;
    private ODKSubmissionData odkSubmissionData;
    private ODKSubmission odkSubmission;
    private HashMap<Long,ArrayList<String>> associatedFacilityPictures;
    private ArrayList<String> associatedProofOfDeliveryPictures;

    private ODKLMISSurveySubmission odkLmisSurveySubmission;
    private ODKRandRSubmission odkRandRSubmission;
    private ODKStorageSurveySubmission odkStorageSurveySubmission;
    private ArrayList<String> pictures;


    Long tempFacilityId;
    ArrayList<String> tempPictures = new ArrayList<String>();

    private Map<String, MultipartFile> submissionPictures;
    private String[] pictureSetMethods = {"setFirstPicture", "setSecondPicture", "setThirdPicture", "setFourthPicture", "setFifthPicture"};

    private static Logger logger = LoggerFactory.getLogger(ODKSubmissionService.class);
    private String formBuildID;

    public void saveODKSubmissionData(Set submissionsFilesSet) throws ODKAccountNotFoundException,
            FacilityPictureNotFoundException, ODKCollectXMLSubmissionFileNotFoundException,
            ODKCollectXMLSubmissionParserConfigurationException, ODKCollectXMLSubmissionSAXException, FacilityNotFoundException, ODKXFormNotFoundException
    {
        // first get the XML submission file
        submissionPictures = new HashMap<String, MultipartFile>();
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
                submissionPictures.put(fileName, (MultipartFile) entry.getValue());
            }
        }

        // first parse the xml file only to get the form id

        SAXParserFactory odkXFormIDParserFactory = SAXParserFactory.newInstance();
        ODKSubmissionXFormIDSAXHandler odkXFormIDHandler = new ODKSubmissionXFormIDSAXHandler();

        try
        {
            SAXParser odkXFormIDParser = odkXFormIDParserFactory.newSAXParser();
            odkXFormIDParser.parse(XMLSubmissionFile.getInputStream(), odkXFormIDHandler);
        }
        catch(SAXException exception )
        {
            throw new ODKCollectXMLSubmissionSAXException();

        }
        catch(IOException e)
        {
            throw new ODKCollectXMLSubmissionFileNotFoundException();
        }
        catch(ParserConfigurationException e)
        {
            throw new ODKCollectXMLSubmissionParserConfigurationException();
        }

        // get the form build id

         formBuildID = odkXFormIDHandler.getFormBuildId();

        // get the xform survey type using the form build id

        ODKXForm tempXForm = odkxFormService.getXFormByFormId(formBuildID);

        if (tempXForm == null)
        {
            throw new ODKXFormNotFoundException();
        }


        ODKXFormSurveyType surveyType = odkxFormService.getXFormSurveyTypeById(tempXForm.getODKXFormSurveyTypeId());


        if(surveyType.getSurveyName().equals("Facility GPS Location and Pictures"))
        {
            saveFacilityGPSLocationAndPicturesSurveyData(XMLSubmissionFile);
        }

        else if (surveyType.getSurveyName().equals("Stock Status"))
        {
            saveStockStatusSurveyData(XMLSubmissionFile);
        }

        else if (surveyType.getSurveyName().equals("Proof of Delivery Survey"))
        {
            saveProofOfDeliverySurveyData(XMLSubmissionFile);
        }

        else if (surveyType.getSurveyName().equals("ZNZ Survey"))
        {
            saveZNZSurveyData(XMLSubmissionFile);
        }

        else
        {
            // Handle
        }

    }

    public void saveZNZSurveyData(MultipartFile XMLSubmissionFile)throws ODKAccountNotFoundException,
            FacilityPictureNotFoundException, ODKCollectXMLSubmissionFileNotFoundException,
            ODKCollectXMLSubmissionParserConfigurationException, ODKCollectXMLSubmissionSAXException
    {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        ODKZNZSurveySubmissionSAXHandler handler;
        handler = new ODKZNZSurveySubmissionSAXHandler();

        String filePath = "/public/odk-collect-submissions/";
        InputStream inputStream;
        FileOutputStream outputStream;

        // first save the xml submission file


        try
        {
            inputStream = XMLSubmissionFile.getInputStream();
            File newFile = new File(servletContext.getRealPath(filePath + XMLSubmissionFile.getOriginalFilename()));
            outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            inputStream.close();
            outputStream.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        // read the submission file into a string

        String xmlString = "";
        try
        {
            File savedSubmissionFile = new File(servletContext.getRealPath(filePath + XMLSubmissionFile.getOriginalFilename()));
            xmlString = readFile(servletContext.getRealPath(filePath + XMLSubmissionFile.getOriginalFilename()), Charset.defaultCharset());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }


        // use XML Formatter

        XmlFormatter formatter = new XmlFormatter();
        String formattedXmlSubmission = formatter.format(xmlString);

        // get input stream from the document object of the formatted xml

        Document  xmlSubmissionDocument;
        try
        {
            xmlSubmissionDocument = stringToDom(formattedXmlSubmission);
        }
        catch(SAXException exception )
        {
            throw new ODKCollectXMLSubmissionSAXException();

        }
        catch(IOException e)
        {
            throw new ODKCollectXMLSubmissionFileNotFoundException();
        }
        catch(ParserConfigurationException e)
        {
            throw new ODKCollectXMLSubmissionParserConfigurationException();
        }

        ByteArrayOutputStream outputStreamForDocument = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(xmlSubmissionDocument);
        Result outputTarget = new StreamResult(outputStreamForDocument);
        try
        {
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);

        }
        catch(TransformerConfigurationException transformerConfigurationException)
        {
            transformerConfigurationException.printStackTrace();
        }
        catch(TransformerException transformerException)
        {
            transformerException.printStackTrace();
        }

        InputStream inputStreamForDocument = new ByteArrayInputStream(outputStreamForDocument.toByteArray());

        try
        {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(inputStreamForDocument, handler);
        }
        catch(SAXException exception )
        {
            throw new ODKCollectXMLSubmissionSAXException();

        }
        catch(IOException e)
        {
            throw new ODKCollectXMLSubmissionFileNotFoundException();
        }
        catch(ParserConfigurationException e)
        {
            throw new ODKCollectXMLSubmissionParserConfigurationException();
        }

        odkSubmission = handler.getOdkSubmission();
        odkAccount = handler.getOdkAccount();
        pictures = handler.getPictures();

        ODKAccount tempAccount;

        tempAccount =  odkAccountService.authenticate(odkAccount);

        odkSubmission.setOdkAccountId(tempAccount.getId());
        odkSubmission.setActive(true);
        this.insertODKSubmission(odkSubmission);
        Long lastODKSubmissionId =  odkSubmissionRepository.getLastSubmissionId();

        // save storage part  with gps coordinates and pictures
        odkStorageSurveySubmission = handler.getOdkStorageSurveySubmission();
        odkStorageSurveySubmission.setODKSubmissionId(lastODKSubmissionId);
        ArrayList<byte[]> facilityPictures = getZNZSurveyPictures();

        for (int count = 0; count < facilityPictures.size(); count ++)
        {
            try
            {
            Method picMethod = odkStorageSurveySubmission.getClass().getMethod(pictureSetMethods[count], new Class[] {byte[].class});
            picMethod.invoke(odkStorageSurveySubmission, facilityPictures.get(count));
            }
            catch (IllegalAccessException ex)
            {
                // TO DO handle
            }

            catch (NoSuchMethodException ex)
            {
                // TO DO handle

            }
            catch (SecurityException ex)
            {
                // TO DO handle
            }
            catch (IllegalArgumentException ex)
            {
                // TO DO handle

            }
            catch (InvocationTargetException ex)
            {
                // TO DO handle
            }
        }

        odkStorageSurveySubmission.setTotalPercentage(getTotalPercentage(odkStorageSurveySubmission, 15));
        odkStorageSurveySubmissionRepository.insert(odkStorageSurveySubmission);

        // save lmis part
        odkLmisSurveySubmission = handler.getOdkLmisSurveySubmission();
        odkLmisSurveySubmission.setODKSubmissionId(lastODKSubmissionId);
        odkLmisSurveySubmission.setTotalPercentage(getTotalPercentage(odkLmisSurveySubmission, 11));
        odklmisSurveySubmissionRepository.insert(odkLmisSurveySubmission);


        // save r and r part
        odkRandRSubmission = handler.getOdkRandRSubmission();
        odkRandRSubmission.setODKSubmissionId(lastODKSubmissionId);
        odkRandRSubmission.setTotalPercentage(getTotalPercentage(odkRandRSubmission, 8));
        odkRandRSubmissionRepository.insert(odkRandRSubmission);

    }

    public ArrayList<byte[]> getZNZSurveyPictures() throws FacilityPictureNotFoundException
    {

        MultipartFile tempPicture;
        InputStream imageInputStream;
        ArrayList<byte[]> facilityPictures = new ArrayList<>();
        for (String fileName:pictures)
        {
            tempPicture = submissionPictures.get(fileName);
            try
            {
                imageInputStream = tempPicture.getInputStream();
                byte[] bytes = IOUtils.toByteArray(imageInputStream);
                facilityPictures.add(bytes);
            }
            catch(IOException e)
            {
                e.printStackTrace();
                throw new FacilityPictureNotFoundException("Facility picture not found.");

            }
            catch(NullPointerException e)
            {
                System.out.println("Picture for:" + fileName + "not found");
            }


        }

        return facilityPictures;
    }

    public double getTotalPercentage(Object obj, int numberOfQuestions) {

        Field[] fields = obj.getClass().getDeclaredFields();
        Field field;
        double value;
        int index;
        double count = 0;
        for(index = 0; index < fields.length ; index ++)
        {
               field = fields[index];
               if ( ((Class) field.getType()).getSimpleName().equals("int")) {
                   field.setAccessible(true);
                   try {
                        value = (int)field.get(obj);
                        count += value == 1 ? 1 : 0;

                   }
                   catch (Exception e) {
                       e.printStackTrace();
                   }
               }
        }

        return (count / numberOfQuestions) * 100;


    }




    public void saveProofOfDeliverySurveyData(MultipartFile XMLSubmissionFile) throws ODKAccountNotFoundException,
            FacilityPictureNotFoundException, ODKCollectXMLSubmissionFileNotFoundException,
            ODKCollectXMLSubmissionParserConfigurationException, ODKCollectXMLSubmissionSAXException, ODKXFormNotFoundException
    {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        ODKProofOfDeliverySubmissionSAXHandler handler;
        handler = new ODKProofOfDeliverySubmissionSAXHandler();

        String filePath = "/public/odk-collect-submissions/";
        InputStream inputStream;
        FileOutputStream outputStream;

        // first save the xml submission file


        try
        {
            inputStream = XMLSubmissionFile.getInputStream();
            File newFile = new File(servletContext.getRealPath(filePath + XMLSubmissionFile.getOriginalFilename()));
            outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            inputStream.close();
            outputStream.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        // read the submission file into a string

        String xmlString = "";
        try
        {
            File savedSubmissionFile = new File(servletContext.getRealPath(filePath + XMLSubmissionFile.getOriginalFilename()));
            xmlString = readFile(servletContext.getRealPath(filePath + XMLSubmissionFile.getOriginalFilename()), Charset.defaultCharset());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }


        // use XML Formatter

        XmlFormatter formatter = new XmlFormatter();
        String formattedXmlSubmission = formatter.format(xmlString);

        // get input stream from the document object of the formatted xml

        Document  xmlSubmissionDocument;
        try
        {
            xmlSubmissionDocument = stringToDom(formattedXmlSubmission);
        }
        catch(SAXException exception )
        {
            throw new ODKCollectXMLSubmissionSAXException();

        }
        catch(IOException e)
        {
            throw new ODKCollectXMLSubmissionFileNotFoundException();
        }
        catch(ParserConfigurationException e)
        {
            throw new ODKCollectXMLSubmissionParserConfigurationException();
        }

        ByteArrayOutputStream outputStreamForDocument = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(xmlSubmissionDocument);
        Result outputTarget = new StreamResult(outputStreamForDocument);
        try
        {
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);

        }
        catch(TransformerConfigurationException transformerConfigurationException)
        {
            transformerConfigurationException.printStackTrace();
        }
        catch(TransformerException transformerException)
        {
            transformerException.printStackTrace();
        }

        InputStream inputStreamForDocument = new ByteArrayInputStream(outputStreamForDocument.toByteArray());

        try
        {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(inputStreamForDocument, handler);
        }
        catch(SAXException exception )
        {
            throw new ODKCollectXMLSubmissionSAXException();

        }
        catch(IOException e)
        {
            throw new ODKCollectXMLSubmissionFileNotFoundException();
        }
        catch(ParserConfigurationException e)
        {
            throw new ODKCollectXMLSubmissionParserConfigurationException();
        }

        odkSubmission = handler.getOdkSubmission();
        odkAccount = handler.getOdkAccount();

        ODKAccount tempAccount;

        tempAccount =  odkAccountService.authenticate(odkAccount);

        odkSubmission.setOdkAccountId(tempAccount.getId());
        odkSubmission.setActive(true);
        this.insertODKSubmission(odkSubmission);
        Long lastODKSubmissionId =  odkSubmissionRepository.getLastSubmissionId();

        odkProofOfDeliverySubmissionData = handler.getOdkProofOfDeliverySubmissionData();

        this.insertODKProofOfDeliverySubmissionData(odkProofOfDeliverySubmissionData);
    }

    public void insertODKProofOfDeliverySubmissionData(ODKProofOfDeliverySubmissionData odkProofOfDeliverySubmissionData)
    {
         Long productId = productService.getByCode(odkProofOfDeliverySubmissionData.getProductCode()).getId();
         odkProofOfDeliverySubmissionData.setProductId(productId);
         // get the pictures

         ODKProofOfDeliverySubmissionData tempODKProofOfDeliverySubmissionData = assignProofOfDeliveryPictures(odkProofOfDeliverySubmissionData);



         odkProofOfDeliverySubmissionDataRepository.insert(tempODKProofOfDeliverySubmissionData);


    }

    public ODKProofOfDeliverySubmissionData assignProofOfDeliveryPictures(ODKProofOfDeliverySubmissionData odkProofOfDeliverySubmissionData)
    {
        MultipartFile tempPicture;
        InputStream imageInputStream;

        int i = 0;
        String tempPictureSetMethod;
        for(String tempFileName : associatedProofOfDeliveryPictures)
        {
            tempPicture = submissionPictures.get(tempFileName);
            tempPictureSetMethod = pictureSetMethods[i];
            try
            {
                imageInputStream = tempPicture.getInputStream();
                byte[] bytes = IOUtils.toByteArray(imageInputStream);
                try
                {
                    Method picMethod = odkProofOfDeliverySubmissionData.getClass().getMethod(tempPictureSetMethod, new Class[] {byte[].class});
                    picMethod.invoke(odkProofOfDeliverySubmissionData, odkProofOfDeliverySubmissionData.getProofOfDeliveryPictures().get(i));
                }

                catch (IllegalAccessException ex)
                {
                    // TO DO handle
                }

                catch (NoSuchMethodException ex)
                {
                    // TO DO handle

                }
                catch (SecurityException ex)
                {
                    // TO DO handle
                }
                catch (IllegalArgumentException ex)
                {
                    // TO DO handle

                }
                catch (InvocationTargetException ex)
                {
                    // TO DO handle
                }

            }
            catch(IOException e)
            {
                e.printStackTrace();


            }
            catch(NullPointerException e)
            {
                System.out.println("Picture for:" + tempFileName + "not found");
            }
        }

        return odkProofOfDeliverySubmissionData;

    }
    public void saveStockStatusSurveyData(MultipartFile XMLSubmissionFile)
            throws ODKAccountNotFoundException,
            FacilityPictureNotFoundException, ODKCollectXMLSubmissionFileNotFoundException,
            ODKCollectXMLSubmissionParserConfigurationException, ODKCollectXMLSubmissionSAXException
    {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        ODKStockStatusSubmissionSAXHandler handler;
        handler = new ODKStockStatusSubmissionSAXHandler();
        // To Do : the xml is not well formatted when stock status submission is done , find out why
        // this is a temporary solution

        String filePath = "/public/odk-collect-submissions/";
        InputStream inputStream;
        FileOutputStream outputStream;

        // first save the xml submission file


        try
        {
        inputStream = XMLSubmissionFile.getInputStream();
        File newFile = new File(servletContext.getRealPath(filePath + XMLSubmissionFile.getOriginalFilename()));
        outputStream = new FileOutputStream(newFile);
        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }

        inputStream.close();
        outputStream.close();
        }
        catch (Exception ex)
        {
             ex.printStackTrace();
        }

        // read the submission file into a string

        String xmlString = "";
        try
        {
            File savedSubmissionFile = new File(servletContext.getRealPath(filePath + XMLSubmissionFile.getOriginalFilename()));
            xmlString = readFile(servletContext.getRealPath(filePath + XMLSubmissionFile.getOriginalFilename()), Charset.defaultCharset());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }


        // use XML Formatter

        XmlFormatter formatter = new XmlFormatter();
        String formattedXmlSubmission = formatter.format(xmlString);

        // get input stream from the document object of the formatted xml

        Document  xmlSubmissionDocument;
        try
        {
            xmlSubmissionDocument = stringToDom(formattedXmlSubmission);
        }
        catch(SAXException exception )
        {
            throw new ODKCollectXMLSubmissionSAXException();

        }
        catch(IOException e)
        {
            throw new ODKCollectXMLSubmissionFileNotFoundException();
        }
        catch(ParserConfigurationException e)
        {
            throw new ODKCollectXMLSubmissionParserConfigurationException();
        }

        ByteArrayOutputStream outputStreamForDocument = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(xmlSubmissionDocument);
        Result outputTarget = new StreamResult(outputStreamForDocument);
        try
        {
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);

        }
        catch(TransformerConfigurationException transformerConfigurationException)
        {
          transformerConfigurationException.printStackTrace();
        }
        catch(TransformerException transformerException)
        {
           transformerException.printStackTrace();
        }

        InputStream inputStreamForDocument = new ByteArrayInputStream(outputStreamForDocument.toByteArray());

        try
        {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(inputStreamForDocument, handler);
        }
        catch(SAXException exception )
        {
            throw new ODKCollectXMLSubmissionSAXException();

        }
        catch(IOException e)
        {
            throw new ODKCollectXMLSubmissionFileNotFoundException();
        }
        catch(ParserConfigurationException e)
        {
            throw new ODKCollectXMLSubmissionParserConfigurationException();
        }

        odkSubmission = handler.getOdkSubmission();
        odkAccount = handler.getOdkAccount();

        ODKAccount tempAccount;

        tempAccount =  odkAccountService.authenticate(odkAccount);

        odkSubmission.setOdkAccountId(tempAccount.getId());
        odkSubmission.setActive(true);
        this.insertODKSubmission(odkSubmission);
        Long lastODKSubmissionId =  odkSubmissionRepository.getLastSubmissionId();

        listODKStockStatusSubmissions = handler.getListODKStockStatusSubmissions();

        for (ODKStockStatusSubmission odkStockStatusSubmission : listODKStockStatusSubmissions)
        {
            odkStockStatusSubmission.setODKSubmissionId(lastODKSubmissionId);
            odkStockStatusSubmission.setActive(true);
            this.insertStockStatus(odkStockStatusSubmission);
        }

    }
    public void saveFacilityGPSLocationAndPicturesSurveyData(MultipartFile XMLSubmissionFile)
            throws ODKAccountNotFoundException,
            FacilityPictureNotFoundException, ODKCollectXMLSubmissionFileNotFoundException,
            ODKCollectXMLSubmissionParserConfigurationException, ODKCollectXMLSubmissionSAXException, FacilityNotFoundException
    {
        // parse the xml file
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        ODKSubmissionSAXHandler handler;
        handler = new ODKSubmissionSAXHandler();

        try
        {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(XMLSubmissionFile.getInputStream(), handler);

        }
        catch(SAXException exception )
        {
            throw new ODKCollectXMLSubmissionSAXException();

        }
        catch(IOException e)
        {
            throw new ODKCollectXMLSubmissionFileNotFoundException();
        }
        catch(ParserConfigurationException e)
        {
            throw new ODKCollectXMLSubmissionParserConfigurationException();
        }

        odkSubmission = handler.getOdkSubmission();
        odkAccount = handler.getOdkAccount();

        ODKAccount tempAccount;

        tempAccount =  odkAccountService.authenticate(odkAccount);

        odkSubmission.setOdkAccountId(tempAccount.getId());
        odkSubmission.setActive(true);
        this.insertODKSubmission(odkSubmission);

        listOfODKSubmissionData = handler.getListOfODKSubmissionData();
        associatedFacilityPictures = handler.getFacilityPictures();
        assignFacilityPictures();
        saveFacilityData();
        saveODKSubmissionData();

    }
    public void assignFacilityPictures() throws FacilityPictureNotFoundException
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
                       tempPicture = submissionPictures.get(fileName);
                       try
                       {
                           imageInputStream = tempPicture.getInputStream();
                           byte[] bytes = IOUtils.toByteArray(imageInputStream);
                           tempData.getFacilityPictures().add(bytes);
                       }
                       catch(IOException e)
                       {
                           e.printStackTrace();
                           throw new FacilityPictureNotFoundException("Facility picture not found.");

                       }
                       catch(NullPointerException e)
                       {
                           System.out.println("Picture for:" + fileName + "not found");
                       }


                   }
               }
            }

        }

    }

    public void saveODKSubmissionData()
    {
        int picturesCount = 0;
        String tempPictureSetMethod;
        for(ODKSubmissionData tempData : this.listOfODKSubmissionData)
        {
            picturesCount = tempData.getFacilityPictures().size();
            for (int i = 0; i < picturesCount; i++)
            {
                tempPictureSetMethod = pictureSetMethods[i];
                try
                {
                    Method picMethod = tempData.getClass().getMethod(tempPictureSetMethod, new Class[] {byte[].class});
                    picMethod.invoke(tempData, tempData.getFacilityPictures().get(i));
                }
                catch (IllegalAccessException ex)
                {
                        // TO DO handle
                }

                catch (NoSuchMethodException ex)
                {
                    // TO DO handle

                }
                catch (SecurityException ex)
                {
                    // TO DO handle
                }
                catch (IllegalArgumentException ex)
                {
                    // TO DO handle

                }
                catch (InvocationTargetException ex)
                {
                    // TO DO handle
                }
            }

            tempData.setODKSubmissionId(odkSubmissionRepository.getLastSubmissionId());
            insertODKSubmissionData(tempData);
        }
    }


    public void saveFacilityData() throws FacilityNotFoundException
    {
        this.currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (ODKSubmissionData ODKfacilitySubmissionData : listOfODKSubmissionData)
        {
            // To Do Add Exception handling if facility is not found
            this.facility = facilityService.getById(ODKfacilitySubmissionData.getFacilityId());
            if (this.facility == null)
            {
                throw new FacilityNotFoundException("Facility with Id :" + ODKfacilitySubmissionData.getFacilityId() + " was not found");
            }
            // To Do Add GPS Accuracy attribute to the facility model so that before updating the,
                // as an exception if the accuracy is lower
            // existing GPS information the accuracy of the existing GPS info is checked.
            this.facility.setLatitude(ODKfacilitySubmissionData.getGPSLatitude());
            this.facility.setLongitude(ODKfacilitySubmissionData.getGPSLongitude());
            this.facility.setAltitude(ODKfacilitySubmissionData.getGPSAltitude());
            this.facility.setModifiedDate(new Date());
            this.facility.setComment(this.facility.getComment() + "\n" + "GPS location captured on " + currentDateTime);
            facilityService.update(this.facility);

        }

    }

    private void insertODKSubmission(ODKSubmission odkSubmission)
    {
        odkSubmissionRepository.insert(odkSubmission);
    }

    private void insertODKSubmissionData(ODKSubmissionData odkSubmissionData)
    {
        odkSubmissionDataRepository.insert(odkSubmissionData);
    }

    private void insertStockStatus(ODKStockStatusSubmission odkStockStatusSubmission)
    {
        odkSubmissionRepository.insertStockStatus(odkStockStatusSubmission);
    }

    private String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static Document stringToDom(String xmlSource)
            throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlSource)));
    }
}
