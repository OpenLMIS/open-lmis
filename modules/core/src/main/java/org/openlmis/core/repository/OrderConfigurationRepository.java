/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.core.repository;

import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.core.repository.mapper.OrderConfigurationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderConfigurationRepository {

  @Autowired
  private OrderConfigurationMapper orderConfigurationMapper;

  public OrderConfiguration getConfiguration() {
    return orderConfigurationMapper.get();
  }

  public void update(OrderConfiguration orderConfiguration) {
    orderConfigurationMapper.update(orderConfiguration);
  }
}
