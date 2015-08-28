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
package org.openlmis.odkapi.controller;

import org.openlmis.odkapi.exception.*;
import org.openlmis.odkapi.service.ODKSubmissionService;
import org.openlmis.odkapi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;



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

        catch(ODKXFormNotFoundException e)
        {
            return new ResponseEntity<String>(this.ODK_COLLECT_ODK_XFORM_NOT_FOUND, this.headers, HttpStatus.NOT_ACCEPTABLE);
        }


      return new ResponseEntity<String>(this.ODK_COLLECT_SUBMISSION_SUCCESSFUL, this.headers, HttpStatus.CREATED);
    }
}
