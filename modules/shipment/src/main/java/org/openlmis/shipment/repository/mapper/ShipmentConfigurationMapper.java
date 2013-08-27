/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.shipment.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentConfigurationMapper {

  @Select("SELECT * FROM shipment_configuration")
  ShipmentConfiguration get();

  @Update({"UPDATE shipment_configuration set headerInFile =  #{headerInFile}"})
  void update(ShipmentConfiguration shipmentConfiguration);

}
