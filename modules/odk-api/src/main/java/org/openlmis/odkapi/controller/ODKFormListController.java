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

import org.openlmis.odkapi.domain.ODKXForm;
import org.openlmis.odkapi.domain.ODKXFormList;
import org.openlmis.odkapi.service.ODKProofOfDeliverySurveyService;
import org.openlmis.odkapi.service.ODKXFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ODKFormListController extends  BaseController
{
    @Autowired
    ODKXFormService odkxFormService;

    @Autowired
    ODKProofOfDeliverySurveyService odkProofOfDeliverySurveyService;

    ODKXForm odkxForm;

    @RequestMapping(value="/odk-api/formList")
    @ResponseBody
    public ResponseEntity<ODKXFormList> getAvailableForms(HttpServletRequest httpServletRequest)
    {
        ODKXFormList odkxFormListXML = odkxFormService.getAvailableXFormDefinitions();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(OPEN_ROSA_VERSION_HEADER, OPEN_ROSA_VERSION);
        responseHeaders.setContentType(MediaType.TEXT_XML);
        return new ResponseEntity<ODKXFormList>(odkxFormListXML, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value="/odk-api/formList/{programDistrictId}")
    @ResponseBody
    public ResponseEntity<ODKXFormList> getAvailableForms(@PathVariable String programDistrictId,HttpServletRequest httpServletRequest)
    {
        // this is for the Proof of Delivery Survey
        ODKXFormList odkxFormListXML = odkProofOfDeliverySurveyService.getActiveODKProofOfDeliveryXFormsByProgramAndDistrictCode(programDistrictId);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(OPEN_ROSA_VERSION_HEADER, OPEN_ROSA_VERSION);
        responseHeaders.setContentType(MediaType.TEXT_XML);
        return new ResponseEntity<ODKXFormList>(odkxFormListXML, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/odk-api/getForm/{formId}")
    @ResponseBody
    public ResponseEntity<String> getForm(@PathVariable String formId, HttpServletRequest httpServletRequest)
    {
        odkxForm = odkxFormService.getXFormByFormId(formId);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(OPEN_ROSA_VERSION_HEADER, OPEN_ROSA_VERSION);
        responseHeaders.setContentType(MediaType.TEXT_XML);
        return new ResponseEntity<String>(odkxForm.getXMLString(), responseHeaders ,HttpStatus.OK);
    }
}
