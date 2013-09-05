/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentFileColumnMapper {

  @Insert({"INSERT INTO shipment_file_columns (name, dataFieldLabel, position, include, mandatory, datePattern) values",
    "(#{name}, #{dataFieldLabel}, #{position}, #{include}, #{mandatory}, #{datePattern})"})
  public void insert(ShipmentFileColumn shipmentFileColumn);

  @Select("SELECT * FROM shipment_file_columns")
  public List<ShipmentFileColumn> getAll();

  @Delete("DELETE FROM shipment_file_columns")
  void deleteAll();
}
