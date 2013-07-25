/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.restapi.service;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.restapi.domain.CHW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RestCHWService {

  @Autowired
  FacilityService facilityService;


  public void create(CHW chw) {
    chw.validate();
    if(getExistingFacilityForCode(chw.getAgentCode()) != null) {
      throw new DataException("error.chw.already.registered");
    }
    Facility facility = getFacilityForCHW(chw);
    facilityService.save(facility);
  }

  private Facility getExistingFacilityForCode(String agentCode) {
    Facility facility = new Facility();
    facility.setCode(agentCode);
    return facilityService.getByCode(facility);
  }

  private Facility getFacilityForCHW(CHW chw) {
    Facility facility = new Facility();
    facility.setCode(chw.getAgentCode());
    facility.setName(chw.getAgentName());
    facility.setMainPhone(chw.getPhoneNumber());
    facility.setActive(chw.getActive() != null ? chw.getActive() : true);
    facility.setVirtualFacility(true);
    facility.setSdp(true);
    facility.setDataReportable(true);
    fillBaseFacility(chw, facility);
    facility.setGoLiveDate(new Date());
    return facility;
  }

  private void fillBaseFacility(CHW chw, Facility facility) {
    Facility baseFacility = getValidatedBaseFacility(chw);
    facility.setParentFacilityId(baseFacility.getId());
    facility.setFacilityType(baseFacility.getFacilityType());
    facility.setGeographicZone(baseFacility.getGeographicZone());
    facility.setOperatedBy(baseFacility.getOperatedBy());
  }

  private Facility getValidatedBaseFacility(CHW chw) {
    Facility baseFacility = facilityService.getFacilityWithReferenceDataForCode(chw.getParentFacilityCode());
    if(baseFacility.getVirtualFacility()) {
      throw new DataException("error.reference.data.parent.facility.virtual");
    }
    return baseFacility;
  }

  public void update(CHW chw) {
    chw.validate();
    if (chw.getActive() == null) {
      throw new DataException("error.restapi.mandatory.missing");
    }

    Facility chwFacility = getExistingFacilityForCode(chw.getAgentCode());
    if (chwFacility == null) {
      throw new DataException("error.invalid.agent.code");
    }

    if(!chwFacility.getVirtualFacility()) {
      throw new DataException("error.chw.not.virtual");
    }
    chwFacility.setName(chw.getAgentName());
    chwFacility.setMainPhone(chw.getPhoneNumber() == null ? chwFacility.getMainPhone() : chw.getPhoneNumber());
    chwFacility.setActive(chw.getActive());
    fillBaseFacility(chw, chwFacility);
    facilityService.update(chwFacility);
  }
}
