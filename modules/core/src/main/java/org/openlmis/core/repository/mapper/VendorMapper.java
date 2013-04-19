/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Vendor;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorMapper {

  @Select("SELECT name, id FROM vendors WHERE name = #{name} AND active = TRUE")
  Vendor getByName(String name);

  @Select("SELECT authToken FROM vendors WHERE name = #{name}")
  String getToken(String name);
}
