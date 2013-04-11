/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Vendor;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorMapper {

  @Insert("INSERT INTO vendors(name,active) VALUES (#{name},#{active})")
  @Options(useGeneratedKeys = true)
  void insert(Vendor vendor);

  @Select("SELECT * FROM vendors WHERE name = #{name}")
  Vendor getByName(String name);
}
