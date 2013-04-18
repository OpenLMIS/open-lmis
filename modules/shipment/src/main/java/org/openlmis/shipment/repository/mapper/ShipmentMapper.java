/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.openlmis.shipment.domain.Shipment;
import org.springframework.stereotype.Repository;


@Repository
public interface ShipmentMapper {

  @Insert({"INSERT INTO shipments (orderNumber, productCode, quantityShipped) " +
     "VALUES" +
     "(#{orderNumber}, #{productCode}, #{quantityShipped})"})
   @Options(useGeneratedKeys = true)
   public Integer insert(Shipment shipment);
}
