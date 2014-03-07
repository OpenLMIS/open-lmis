/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.restapi.service;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroupMember;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramSupportedService;
import org.openlmis.core.service.RequisitionGroupMemberService;
import org.openlmis.core.service.UserService;
import org.openlmis.restapi.domain.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * This service exposes methods for managing Agent(a Facility-Like entity which can be associated with an Rnr e.g. CHW or a Virtual Facility)
 */

@Service
public class RestAgentService {

  @Autowired
  FacilityService facilityService;

  @Autowired
  UserService userService;

  @Autowired
  ProgramSupportedService programSupportedService;

  @Autowired
  RequisitionGroupMemberService requisitionGroupMemberService;

  @Transactional
  public void create(Agent agent, Long userId) {
    agent.validate();
    if (getExistingFacilityForCode(agent.getAgentCode()) != null) {
      throw new DataException("error.agent.already.registered");
    }
    Facility facility = createFacilityFrom(agent);
    facility.setCreatedBy(userId);
    facility.setModifiedBy(userId);
    facilityService.save(facility);
    programSupportedService.updateSupportedPrograms(facility);
    saveRequisitionGroupMembers(facility, userId);
  }

  private void saveRequisitionGroupMembers(Facility facility, Long userId) {
    List<RequisitionGroupMember> requisitionGroupMembers =
      requisitionGroupMemberService.getAllRequisitionGroupMembersByFacility(facility.getParentFacilityId());
    for (RequisitionGroupMember requisitionGroupMember : requisitionGroupMembers) {
      RequisitionGroupMember member = new RequisitionGroupMember(requisitionGroupMember.getRequisitionGroup(), facility);
      member.setCreatedBy(userId);
      member.setModifiedBy(userId);
      member.setModifiedDate(new Date());
      requisitionGroupMemberService.save(member);
    }
  }

  public void update(Agent agent, Long userId) {
    if (agent.getActive() == null) {
      throw new DataException("error.mandatory.fields.missing");
    }
    agent.validate();

    Facility chwFacility = getExistingFacilityForCode(agent.getAgentCode());
    validateCHWForUpdate(chwFacility);

    chwFacility.setName(agent.getAgentName());
    chwFacility.setMainPhone(agent.getPhoneNumber() == null ? chwFacility.getMainPhone() : agent.getPhoneNumber());
    chwFacility.setActive(Boolean.parseBoolean(agent.getActive()));
    Long previousParent = chwFacility.getParentFacilityId();
    fillBaseFacility(agent, chwFacility);
    if (!previousParent.equals(chwFacility.getParentFacilityId())) {
      requisitionGroupMemberService.deleteMembersFor(chwFacility);
      saveRequisitionGroupMembers(chwFacility, userId);
    }
    chwFacility.setModifiedDate(new Date());
    chwFacility.setModifiedBy(userId);
    facilityService.update(chwFacility);
  }

  private Facility getExistingFacilityForCode(String agentCode) {
    Facility facility = new Facility();
    facility.setCode(agentCode);
    return facilityService.getByCode(facility);
  }

  private Facility createFacilityFrom(Agent agent) {
    Facility facility = new Facility();
    facility.setCode(agent.getAgentCode());
    facility.setName(agent.getAgentName());
    facility.setMainPhone(agent.getPhoneNumber());
    facility.setActive(Boolean.parseBoolean(agent.getActive()));
    facility.setVirtualFacility(true);
    facility.setSdp(true);
    facility.setEnabled(true);
    fillBaseFacility(agent, facility);
    facility.setGoLiveDate(new Date());
    return facility;
  }

  private void fillBaseFacility(Agent agent, Facility facility) {
    Facility baseFacility = getValidatedBaseFacility(agent);
    facility.setParentFacilityId(baseFacility.getId());
    facility.setFacilityType(baseFacility.getFacilityType());
    facility.setGeographicZone(baseFacility.getGeographicZone());
    facility.setSupportedPrograms(baseFacility.getSupportedPrograms());
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

    if (!chwFacility.getEnabled()) {
      throw new DataException("error.agent.deleted");
    }
  }
}
