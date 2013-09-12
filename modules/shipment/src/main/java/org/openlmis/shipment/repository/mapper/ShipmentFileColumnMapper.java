/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentFileColumnMapper {

  @Insert({"UPDATE shipment_file_columns SET",
    "position = #{position},",
    "include = #{include},",
    "datePattern = #{datePattern},",
    "modifiedBy = #{modifiedBy},",
    "modifiedDate = #{modifiedDate}",
    "WHERE name = #{name}"})
  public void update(ShipmentFileColumn shipmentFileColumn);

  @Select("SELECT * FROM shipment_file_columns")
  public List<ShipmentFileColumn> getAll();

}
