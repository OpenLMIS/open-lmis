/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.core.repository;

import org.openlmis.core.domain.DeliveryZoneMember;
import org.openlmis.core.repository.mapper.DeliveryZoneMemberMapper;
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
