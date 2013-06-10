/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.allocation.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.allocation.domain.DeliveryZone;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryZoneMapper {

  @Insert({"INSERT INTO delivery_zones (code, name, description, createdBy, modifiedBy, modifiedDate)",
  "VALUES (#{code}, #{name}, #{description}, #{createdBy}, #{modifiedBy}, #{modifiedDate})"})
  @Options(useGeneratedKeys = true)
  void insert(DeliveryZone zone);

  @Update({"UPDATE delivery_zones SET code = #{code}, name = #{name}, description = #{description}, modifiedBy = #{modifiedBy},",
    "modifiedDate = #{modifiedDate} WHERE id = #{id}"})
  void update(DeliveryZone zone);

  @Select({"SELECT * FROM delivery_zones WHERE id = #{id}"})
  DeliveryZone getById(Long id);

  @Select({"SELECT * FROM delivery_zones WHERE LOWER(code) = LOWER(#{code})"})
  DeliveryZone getByCode(String code);
}
