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

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.core.service.ProcessingPeriodService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.odkapi.domain.ODKProofOfDeliveryXForm;
import org.openlmis.odkapi.domain.ODKXForm;
import org.openlmis.odkapi.domain.ODKXFormDTO;
import org.openlmis.odkapi.domain.ODKXFormList;
import org.openlmis.odkapi.parser.ODKProofOfDeliveryXFormBuilder;
import org.openlmis.odkapi.parser.XmlFormatter;
import org.openlmis.odkapi.repository.ODKProofOfDeliveryXFormRepository;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ODKProofOfDeliverySurveyService {

    @Autowired
    private RequisitionService requisitionService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProcessingPeriodService processingPeriodService;

    @Autowired
    private GeographicZoneService geographicZoneService;

    @Autowired
    private ODKXFormService odkxFormService;

    @Autowired
    private ODKProofOfDeliveryXFormRepository odkProofOfDeliveryXFormRepository;

    private ODKProofOfDeliveryXFormBuilder odkProofOfDeliveryXFormBuilder;

    private XmlFormatter xmlFormatter;

    public boolean createODKXFormsForReleasedRequisition(Long requisitionId)
    {
        // first get all the requisition line items list
        Rnr requisition = requisitionService.getFullRequisitionById(requisitionId);
        List<RnrLineItem> rnrLineItemList = requisition.getAllLineItems();

        // get the facility
        Facility facility = facilityService.getById(requisition.getFacility().getId());

        // get the program
        Program program = programService.getById(requisition.getProgram().getId());

        // get the period string - start_date-end_date
        ProcessingPeriod period = requisition.getPeriod();
        String startDateString  = "";
        String endDateString    = "";
        try
        {
            startDateString = period.getStringStartDate();
            endDateString   = period.getStringEndDate();

        }
        catch (ParseException pException)
        {
            // handle
        }

        String processingPeriodString = startDateString.concat("-").concat(endDateString);


        // get the district

        GeographicZone district = facility.getGeographicZone();

        odkProofOfDeliveryXFormBuilder = new ODKProofOfDeliveryXFormBuilder();

        String proofOfDeliveryXForm = odkProofOfDeliveryXFormBuilder.buildXForm(facility.getName(),
                program.getCode(), processingPeriodString, rnrLineItemList);
        xmlFormatter = new XmlFormatter();
        String formattedXML = xmlFormatter.format(proofOfDeliveryXForm);

        // save the xform
        String formId   =   facility.getCode().concat("-").concat(program.getCode()).concat("-").concat(district.getCode()).concat("-").concat("-").concat(processingPeriodString);
        ODKXForm odkxForm = new ODKXForm();
        odkxForm.setFormId(formId);
        odkxForm.setName("Proof of Delivery Survey");
        odkxForm.setVersion("2.0");
        odkxForm.setHash("");
        odkxForm.setDescriptionText("ODK XForm used to collect proof of delivery survey");
        odkxForm.setDownloadUrl("http://uat.tz.elmis-dev.org/odk-api/getForm/"+odkxForm.getFormId());
        odkxForm.setXMLString(formattedXML);
        odkxForm.setActive(true);
        odkxForm.setODKXFormSurveyTypeId(Long.parseLong("3"));

        odkxFormService.save(odkxForm);

        //save proof of delivery xform record

        ODKProofOfDeliveryXForm odkProofOfDeliveryXForm = new ODKProofOfDeliveryXForm();
        odkProofOfDeliveryXForm.setFacilityId(facility.getId());
        odkProofOfDeliveryXForm.setProgramId(program.getId());
        odkProofOfDeliveryXForm.setDistrictId(district.getId());
        odkProofOfDeliveryXForm.setProgramId(period.getId());
        odkProofOfDeliveryXForm.setRnrId(requisition.getId());

        // get the odk xform using the formid
        ODKXForm tempODKXForm = odkxFormService.getXFormByFormId(formId);
        odkProofOfDeliveryXForm.setOdkXformId(tempODKXForm.getId());
        odkProofOfDeliveryXForm.setActive(true);
        // finally save the proof of delivery xform
         save(odkProofOfDeliveryXForm);

         return true;
    }

    public ODKXFormList getActiveODKProofOfDeliveryXFormsByProgramAndDistrictCode(String programDistrictCode)
    {

        String[] tempProgramDistrictCodes = programDistrictCode.split("-");
        String programCode = tempProgramDistrictCodes[0];
        String districtCode = tempProgramDistrictCodes[1];

        Program program = programService.getByCode(programCode);
        GeographicZone tempGeographicZone = new GeographicZone();
        tempGeographicZone.setCode(districtCode);
        GeographicZone districtGeographicZone = geographicZoneService.getByCode(tempGeographicZone);
        ODKXFormList odkxFormListXML = new ODKXFormList();

        List<ODKProofOfDeliveryXForm> proofOfDeliveryXForms = odkProofOfDeliveryXFormRepository.getActiveODKProofOfDeliveryXFormsByProgramAndDistrictIds(program.getId(), districtGeographicZone.getId());

        List<ODKXForm> odkxFormList = new ArrayList<>();
        ODKXForm odkxForm;

        for(ODKProofOfDeliveryXForm odkProofOfDeliveryXForm : proofOfDeliveryXForms)
        {
            odkxForm = odkxFormService.getXFormById(odkProofOfDeliveryXForm.getOdkXformId());
            odkxFormList.add(odkxForm);
        }

        List<ODKXFormDTO> odkxFormDTOs = new ArrayList<>();

        for(ODKXForm tempODKXForm : odkxFormList)
        {
            ODKXFormDTO temp = new ODKXFormDTO();
            temp.setFormID(tempODKXForm.getFormId());
            temp.setName(tempODKXForm.getName());
            temp.setVersion(tempODKXForm.getVersion());
            temp.setDescriptionText(tempODKXForm.getDescriptionText());
            temp.setHash(tempODKXForm.getHash());
            temp.setDownloadUrl(tempODKXForm.getDownloadUrl());
            odkxFormDTOs.add(temp);
        }

        odkxFormListXML.odkxFormList = odkxFormDTOs;

        return odkxFormListXML;
    }

    public void save(ODKProofOfDeliveryXForm odkProofOfDeliveryXForm)
    {
        odkProofOfDeliveryXFormRepository.insert(odkProofOfDeliveryXForm);
    }






}
