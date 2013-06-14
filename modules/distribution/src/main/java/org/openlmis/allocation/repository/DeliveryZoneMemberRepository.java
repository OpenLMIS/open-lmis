package org.openlmis.allocation.repository;

import org.openlmis.allocation.domain.DeliveryZoneMember;
import org.openlmis.allocation.repository.mapper.DeliveryZoneMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeliveryZoneMemberRepository {

  @Autowired
  DeliveryZoneMemberMapper mapper;

  public void insert(DeliveryZoneMember member) {
    mapper.insert(member);
  }

  public void update(DeliveryZoneMember member) {
    mapper.update(member);
  }

  public DeliveryZoneMember getByDeliveryZoneCodeAndFacilityCode(String deliveryZoneCode, String facilityCode) {
    return mapper.getByDeliveryZoneCodeAndFacilityCode(deliveryZoneCode, facilityCode);
  }

  public List<Long> getDeliveryZoneProgramIdsForFacility(Long facilityId) {
    return mapper.getDeliveryZoneProgramIdsForFacility(facilityId);
  }
}
