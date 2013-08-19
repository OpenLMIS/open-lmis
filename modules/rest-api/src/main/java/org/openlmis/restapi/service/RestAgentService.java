/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.restapi.service;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.VendorService;
import org.openlmis.restapi.domain.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class RestAgentService {

  @Autowired
  FacilityService facilityService;

  @Autowired
  VendorService vendorService;

  @Transactional
  public void create(Agent agent, String userName) {
    agent.validate();
    if (getExistingFacilityForCode(agent.getAgentCode()) != null) {
      throw new DataException("error.agent.already.registered");
    }
    Facility facility = getFacilityForCHW(agent);
    facility.setCreatedBy(vendorService.getByName(userName).getId());
    facility.setModifiedBy(facility.getCreatedBy());
    facilityService.save(facility);
  }

  public void update(Agent agent, String userName) {
    if (agent.getActive() == null) {
      throw new DataException("error.restapi.mandatory.missing");
    }
    agent.validate();

    Facility chwFacility = getExistingFacilityForCode(agent.getAgentCode());
    validateCHWForUpdate(chwFacility);

    chwFacility.setName(agent.getAgentName());
    chwFacility.setMainPhone(agent.getPhoneNumber() == null ? chwFacility.getMainPhone() : agent.getPhoneNumber());
    chwFacility.setActive(Boolean.parseBoolean(agent.getActive()));
    fillBaseFacility(agent, chwFacility);
    chwFacility.setModifiedDate(new Date());
    chwFacility.setModifiedBy(vendorService.getByName(userName).getId());
    facilityService.update(chwFacility);
  }

  private Facility getExistingFacilityForCode(String agentCode) {
    Facility facility = new Facility();
    facility.setCode(agentCode);
    return facilityService.getByCode(facility);
  }

  private Facility getFacilityForCHW(Agent agent) {
    Facility facility = new Facility();
    facility.setCode(agent.getAgentCode());
    facility.setName(agent.getAgentName());
    facility.setMainPhone(agent.getPhoneNumber());
    facility.setActive(Boolean.parseBoolean(agent.getActive()));
    facility.setVirtualFacility(true);
    facility.setSdp(true);
    facility.setDataReportable(true);
    fillBaseFacility(agent, facility);
    facility.setGoLiveDate(new Date());
    return facility;
  }

  private void fillBaseFacility(Agent agent, Facility facility) {
    Facility baseFacility = getValidatedBaseFacility(agent);
    facility.setParentFacilityId(baseFacility.getId());
    facility.setFacilityType(baseFacility.getFacilityType());
    facility.setGeographicZone(baseFacility.getGeographicZone());
    facility.setOperatedBy(baseFacility.getOperatedBy());
  }

  private Facility getValidatedBaseFacility(Agent agent) {
    Facility baseFacility = facilityService.getFacilityWithReferenceDataForCode(agent.getParentFacilityCode());
    if (baseFacility.getVirtualFacility()) {
      throw new DataException("error.reference.data.parent.facility.virtual");
    }
    return baseFacility;
  }

  private void validateCHWForUpdate(Facility chwFacility) {
    if (chwFacility == null) {
      throw new DataException("error.invalid.agent.code");
    }

    if (!chwFacility.getVirtualFacility()) {
      throw new DataException("error.agent.not.virtual");
    }

    if (!chwFacility.getDataReportable()) {
      throw new DataException("error.agent.deleted");
    }
  }
}
