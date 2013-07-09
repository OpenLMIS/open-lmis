/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.ProductGroup;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductGroupMapper {

  @Insert("INSERT INTO product_groups(code, name, createdBy, modifiedDate, modifiedBy) VALUES (#{code}, #{name}, #{createdBy}, #{modifiedDate}, #{modifiedBy})")
  @Options(useGeneratedKeys = true)
  public void insert(ProductGroup productGroup);

  @Select("SELECT * FROM product_groups WHERE code=#{code}")
  ProductGroup getByCode(String code);

  @Update("UPDATE product_groups SET code = #{code}, name = #{name}, modifiedDate = #{modifiedDate}, modifiedBy = #{modifiedBy} WHERE id = #{id}")
  void update(ProductGroup productGroup);

  @Select("SELECT * FROM product_groups WHERE id=#{id}")
  ProductGroup getById(Long id);
}
