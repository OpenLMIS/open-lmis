package org.openlmis.allocation.handler;

import org.openlmis.allocation.domain.DeliveryZoneMember;
import org.openlmis.allocation.service.DeliveryZoneMemberService;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.upload.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeliveryZoneMemberHandler extends AbstractModelPersistenceHandler {

  private static final String DUPLICATE_DELIVERY_ZONE_CODE_AND_MEMBER_CODE_COMBINATION_ERROR = "error.deliveryZone.member.duplicate";
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
    return DUPLICATE_DELIVERY_ZONE_CODE_AND_MEMBER_CODE_COMBINATION_ERROR;
  }
}
