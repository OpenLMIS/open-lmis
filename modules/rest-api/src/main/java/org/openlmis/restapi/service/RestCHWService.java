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
    if(exists(chw.getAgentCode())) {
      throw new DataException("error.chw.already.registered");
    }
    Facility facility = getFacilityForCHW(chw);
    facilityService.save(facility);
  }

  private boolean exists(String agentCode) {
    Facility facility  = new Facility();
    facility.setCode(agentCode);
    return facilityService.getByCode(facility) != null;
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
    Facility baseFacility = getValidatedBaseFacility(chw);
    facility.setParentFacilityId(baseFacility.getId());
    facility.setFacilityType(baseFacility.getFacilityType());
    facility.setGeographicZone(baseFacility.getGeographicZone());
    facility.setOperatedBy(baseFacility.getOperatedBy());
    facility.setGoLiveDate(new Date());
    return facility;

  }

  private Facility getValidatedBaseFacility(CHW chw) {
    Facility baseFacility = facilityService.getFacilityWithReferenceDataForCode(chw.getParentFacilityCode());
    if(baseFacility.getVirtualFacility()) {
      throw new DataException("error.reference.data.parent.facility.virtual");
    }
    return baseFacility;
  }
}
