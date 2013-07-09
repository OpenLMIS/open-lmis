/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.openlmis.distribution.domain.Distribution;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributionMapper {

  @Insert({"INSERT INTO distributions",
    "(deliveryZoneId, programId, periodId, status, createdBy, modifiedBy)",
    "VALUES",
    "(#{deliveryZone.id}, #{program.id}, #{period.id}, #{status}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  void insert(Distribution distribution);
}
