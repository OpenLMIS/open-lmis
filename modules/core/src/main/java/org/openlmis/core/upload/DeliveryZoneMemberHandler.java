/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.DeliveryZoneMember;
import org.openlmis.core.service.DeliveryZoneMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * DeliveryZoneMemberHandler is used for uploads of DeliveryZone members (facilities).
 * It uploads each DeliveryZoneMember record by record.
 */
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
