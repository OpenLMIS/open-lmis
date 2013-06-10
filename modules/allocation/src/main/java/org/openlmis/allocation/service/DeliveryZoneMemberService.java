package org.openlmis.allocation.service;

import org.openlmis.allocation.domain.DeliveryZone;
import org.openlmis.allocation.domain.DeliveryZoneMember;
import org.openlmis.allocation.repository.DeliveryZoneMemberRepository;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryZoneMemberService {

  public static final String FACILITY_CODE_INVALID = "facility.code.invalid";

  @Autowired
  DeliveryZoneMemberRepository repository;

  @Autowired
  FacilityService facilityService;

  @Autowired
  DeliveryZoneService deliveryZoneService;

  public void save(DeliveryZoneMember member) {
    fillFacility(member);
    fillDeliveryZone(member);
    if (member.getId() == null)
      repository.insert(member);
    else
      repository.update(member);
  }

  private void fillDeliveryZone(DeliveryZoneMember member) {
    DeliveryZone zone = deliveryZoneService.getByCode(member.getDeliveryZone().getCode());
    if(zone == null) throw new DataException("deliveryZone.code.invalid");
    member.setDeliveryZone(zone);
  }

  private void fillFacility(DeliveryZoneMember member) {
    Facility facility = facilityService.getByCode(member.getFacility());
    if(facility == null) throw new DataException(FACILITY_CODE_INVALID);
    member.setFacility(facility);
  }

  public DeliveryZoneMember getByDeliveryZoneCodeAndFacilityCode(String deliveryZoneCode, String facilityCode) {
    return repository.getByDeliveryZoneCodeAndFacilityCode(deliveryZoneCode, facilityCode);
  }
}
