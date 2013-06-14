package org.openlmis.distribution.handler;

import org.openlmis.distribution.domain.DeliveryZoneMember;
import org.openlmis.distribution.service.DeliveryZoneMemberService;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.upload.AbstractModelPersistenceHandler;
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
  protected String getDuplicateMessageKey() {
    return "error.duplicate.delivery.zone.member";
  }
}
