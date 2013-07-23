package org.openlmis.restapi.service;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.restapi.domain.CHW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestCHWService {

  @Autowired
  FacilityService facilityService;


  public void create(CHW chw) {
    Facility facility = getFacilityForCHW(chw);
    facilityService.save(facility);
  }

  private Facility getFacilityForCHW(CHW chw) {
    Facility facility = new Facility();
    facility.setCode(chw.getAgentCode());
    facility.setName(chw.getAgentName());
    facility.setMainPhone(chw.getPhoneNumber());
    facility.setActive(chw.getActive());
    facility.setVirtualFacility(true);
    facility.setSdp(true);
    facility.setDataReportable(true);

    Facility baseCHWFacility = new Facility();
    baseCHWFacility.setCode(chw.getBaseFacilityCode());
    Facility baseFacility = facilityService.getByCode(baseCHWFacility);
    facility.setParentFacilityId(baseFacility.getId());
    facility.setFacilityType(baseFacility.getFacilityType());
    facility.setGeographicZone(baseFacility.getGeographicZone());

    return facility;

  }
}
