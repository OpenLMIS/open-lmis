/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.ProductGroup;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductGroupMapper {

  @Insert("INSERT INTO product_groups(code, name) VALUES (#{code}, #{name})")
  public void insert(ProductGroup productGroup);

  @Select("Select * from product_groups where code=#{code}")
  ProductGroup getByCode(String code);
}
