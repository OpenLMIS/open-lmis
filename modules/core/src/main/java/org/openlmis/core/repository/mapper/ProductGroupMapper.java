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
import org.openlmis.core.domain.ProductGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProductGroupMapper maps the ProductGroup entity to corresponding representation in database.
 */
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

  @Select("SELECT * FROM product_groups")
  List<ProductGroup> getAll();
}
