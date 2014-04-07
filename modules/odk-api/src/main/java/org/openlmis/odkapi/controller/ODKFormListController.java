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

import org.openlmis.odkapi.domain.ODKXForm;
import org.openlmis.odkapi.domain.ODKXFormList;
import org.openlmis.odkapi.domain.ODKXFormDTO;
import org.openlmis.odkapi.repository.ODKXFormRepository;
import org.openlmis.odkapi.service.ODKXFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ODKFormListController extends  BaseController
{
    @Autowired
    ODKXFormService odkxFormService;

    ODKXForm odkxForm;

    @RequestMapping(value="/odk-api/formList")
    @ResponseBody
    public ResponseEntity<ODKXFormList> getAvailableForms()
    {
        ODKXFormList odkxFormListXML = odkxFormService.getAvailableXFormDefinitions();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(OPEN_ROSA_VERSION_HEADER, OPEN_ROSA_VERSION);
        responseHeaders.setContentType(MediaType.TEXT_XML);
        return new ResponseEntity<ODKXFormList>(odkxFormListXML, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/odk-api/getForm/{formId}")
    @ResponseBody
    public ResponseEntity<String> getForm(@PathVariable String formId)
    {
        odkxForm = odkxFormService.getXFormByFormId(formId);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(OPEN_ROSA_VERSION_HEADER, OPEN_ROSA_VERSION);
        responseHeaders.setContentType(MediaType.TEXT_XML);
        return new ResponseEntity<String>(odkxForm.getXMLString(), responseHeaders ,HttpStatus.OK);
    }
}
