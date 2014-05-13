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
package org.openlmis.odkapi.controller;

/*
import org.openlmis.web.logger.ApplicationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
*/
import org.openlmis.odkapi.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.openlmis.odkapi.parser.ODKSubmissionSAXHandler;
import org.openlmis.odkapi.service.ODKSubmissionService;
import org.openlmis.odkapi.util.DateUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;



@Controller
public class ODKSubmissionController extends BaseController {

    public HttpHeaders headers ;
    private long milliseconds;
    private String dateFormat;
    @Autowired
    ODKSubmissionService odkSubmissionService;

    @Autowired
    DateUtil dateUtil;

    @RequestMapping(value="/odk-api/submission", method=RequestMethod.HEAD)
    public ResponseEntity<String> handleHeadRequest(HttpServletRequest httpServletRequest)
    {    // do the authentication here
        this.headers = new HttpHeaders();
        this.dateFormat = "yyyy-MM-dd HH:mm:ss";
        this.headers.set(OPEN_ROSA_VERSION_HEADER, OPEN_ROSA_VERSION);
        this.headers.set(OPEN_ROSA_ACCEPT_CONTENT_LENGTH_HEADER, OPEN_ROSA_ACCEPT_CONTENT_LENGTH);
        this.milliseconds = dateUtil.getCurrentDateByFormat(this.dateFormat).getTime();
        this.headers.setDate(this.milliseconds);
        return new ResponseEntity<String>(this.ODK_COLLECT_HEAD_REQUEST_SUCCESSFUL, this.headers, HttpStatus.NO_CONTENT);

    }

    @RequestMapping(value="/odk-api/submission", method=RequestMethod.POST)
    public ResponseEntity<String> processSubmission(
            @RequestParam("xml_submission_file") MultipartFile XMLSubmissionFile, HttpServletRequest request)
    {
        this.headers = new HttpHeaders();
        this.dateFormat ="yyyy-MM-dd HH:mm:ss";
        this.headers.set(OPEN_ROSA_VERSION_HEADER, OPEN_ROSA_VERSION);
        this.headers.set(OPEN_ROSA_ACCEPT_CONTENT_LENGTH_HEADER, OPEN_ROSA_ACCEPT_CONTENT_LENGTH);
        this.milliseconds = dateUtil.getCurrentDateByFormat(this.dateFormat).getTime();
        this.headers.setDate(this.milliseconds);

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        Set submissionsFilesSet = multipartRequest.getFileMap().entrySet();

        try
        {
            odkSubmissionService.saveODKSubmissionData(submissionsFilesSet);
        }

        catch (ODKAccountNotFoundException e)
        {
            return new ResponseEntity<String>(this.ODK_ACCOUNT_NOT_VALID, this.headers, HttpStatus.FORBIDDEN);
        }

        catch (FacilityNotFoundException e)
        {
            return new ResponseEntity<String>(this.FACILITY_NOT_FOUND, this.headers, HttpStatus.FORBIDDEN);
        }

        catch (FacilityPictureNotFoundException e)
        {
            return new ResponseEntity<String>(this.FACILITY_PICTURE_MISSING, this.headers, HttpStatus.NOT_ACCEPTABLE);
        }

        catch(ODKCollectXMLSubmissionFileNotFoundException e)
        {
            return new ResponseEntity<String>(this.ODK_COLLECT_XML_SUBMISSION_FILE_MISSING, this.headers, HttpStatus.NOT_ACCEPTABLE);
        }
        catch(ODKCollectXMLSubmissionSAXException e)
        {
            return new ResponseEntity<String>(this.ODK_COLLECT_XML_SUBMISSION_FILE_PARSE_ERROR, this.headers, HttpStatus.NOT_ACCEPTABLE);
        }
        catch(ODKCollectXMLSubmissionParserConfigurationException e)
        {
            return new ResponseEntity<String>(this.ODK_COLLECT_XML_SUBMISSION_FILE_PARSE_ERROR, this.headers, HttpStatus.NOT_ACCEPTABLE);
        }


      return new ResponseEntity<String>(this.ODK_COLLECT_SUBMISSION_SUCCESSFUL, this.headers, HttpStatus.CREATED);
    }
}
