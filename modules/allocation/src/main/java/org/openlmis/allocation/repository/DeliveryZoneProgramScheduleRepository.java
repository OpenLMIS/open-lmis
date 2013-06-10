/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.allocation.repository;

import org.openlmis.allocation.domain.DeliveryZoneProgramSchedule;
import org.openlmis.allocation.repository.mapper.DeliveryZoneProgramScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DeliveryZoneProgramScheduleRepository {

  @Autowired
  DeliveryZoneProgramScheduleMapper mapper;

  public void insert(DeliveryZoneProgramSchedule schedule) {
    mapper.insert(schedule);
  }

  public void update(DeliveryZoneProgramSchedule schedule) {
    mapper.update(schedule);
  }

  public DeliveryZoneProgramSchedule getByDeliveryZoneCodeAndProgramCode(String deliveryZoneCode, String programCode) {
    return mapper.getByDeliveryZoneCodeAndProgramCode(deliveryZoneCode, programCode);
  }
}
