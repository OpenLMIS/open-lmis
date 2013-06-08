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
import org.openlmis.core.domain.ProductCategory;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductCategoryMapper {


  @Insert({"INSERT INTO product_categories",
      "(code, name, displayOrder, modifiedBy, modifiedDate)",
      "VALUES",
      "(#{code}, #{name}, #{displayOrder}, #{modifiedBy}, COALESCE(#{modifiedDate}, NOW()))"})
  @Options(useGeneratedKeys = true)
  public void insert(ProductCategory productCategory);

  @Select("SELECT * FROM product_categories WHERE id = #{id}")
  public ProductCategory getProductCategoryById(Long id);

  @Select("SELECT * FROM product_categories WHERE LOWER(code) = LOWER(#{code})")
  public ProductCategory getProductCategoryByCode(String code);

  @Update({"UPDATE product_categories SET name = #{name}, modifiedBy = #{modifiedBy},",
      "displayOrder = #{displayOrder}, modifiedDate =#{modifiedDate} where id = #{id}"})
  public void update(ProductCategory category);

  @Select("SELECT id FROM product_categories WHERE LOWER(code) = LOWER(#{code})")
  public Long getProductCategoryIdByCode(String categoryCode);
}
