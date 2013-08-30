package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.DeliveryZoneMember;
import org.openlmis.core.service.DeliveryZoneMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeliveryZoneMemberHandler extends AbstractModelPersistenceHandler {

  @Autowired
  DeliveryZoneMemberService service;

  @Override
  protected BaseModel getExisting(BaseModel record) {
    DeliveryZoneMember member = (DeliveryZoneMember) record;
    return service.getByDeliveryZoneCodeAndFacilityCode(member.getDeliveryZone().getCode(), member.getFacility().getCode());
  }

  @Override
  protected void save(BaseModel record) {
    service.save((DeliveryZoneMember) record);
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.delivery.zone.member";
  }
}
