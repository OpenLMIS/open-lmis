/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
      "(code, name, displayOrder, createdBy, modifiedBy, modifiedDate)",
      "VALUES",
      "(#{code}, #{name}, #{displayOrder}, #{createdBy}, #{modifiedBy}, COALESCE(#{modifiedDate}, NOW()))"})
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
