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

import org.openlmis.odkapi.domain.ODKXForm;
import org.openlmis.odkapi.domain.ODKXFormDTO;
import org.openlmis.odkapi.domain.ODKXFormList;
import org.openlmis.odkapi.repository.ODKXFormRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ODKXFormService {

    @Autowired
    ODKXFormRepository odkxFormRepository;

    public ODKXFormList getAvailableXFormDefinitions()
    {
        ODKXFormList odkxFormListXML = new ODKXFormList();
        List<ODKXFormDTO> odkxFormDTOs = new ArrayList<>();

        List<ODKXForm> odkxFormList = odkxFormRepository.getAvailableXFormDefinitions();
        for(ODKXForm odkxForm : odkxFormList)
        {
            ODKXFormDTO temp = new ODKXFormDTO();
            temp.setFormID(odkxForm.getFormId());
            temp.setName(odkxForm.getName());
            temp.setVersion(odkxForm.getVersion());
            temp.setDescriptionText(odkxForm.getDescriptionText());
            temp.setHash(odkxForm.getHash());
            temp.setDownloadUrl(odkxForm.getDownloadUrl());
            odkxFormDTOs.add(temp);
        }

        odkxFormListXML.odkxFormList = odkxFormDTOs;

        return odkxFormListXML;


    }

    public ODKXForm getXFormByFormId(String formId)
    {
        return odkxFormRepository.getXFormByFormId(formId);
    }
}
