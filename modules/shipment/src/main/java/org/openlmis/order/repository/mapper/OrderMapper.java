/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.openlmis.order.domain.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMapper {

  @Insert("INSERT INTO orders(rnrId, fulfilled, createdBy) VALUES (#{rnrId}, #{fulfilled}, #{createdBy})")
  @Options(useGeneratedKeys = true)
  void insert(Order order);
}
