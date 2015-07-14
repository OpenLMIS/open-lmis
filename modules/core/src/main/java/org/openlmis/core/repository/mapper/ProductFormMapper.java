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

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.ProductForm;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProductFormMapper maps the ProductForm entity to corresponding representation in database.
 */
@Repository
public interface ProductFormMapper {

  // Used by ProductMapper
  @SuppressWarnings("unused")
  @Select("SELECT * FROM product_forms WHERE id = #{id}")
  ProductForm getById(Long id);

  @Select("SELECT * FROM product_forms")
  List<ProductForm> getAll();

  @Select("SELECT * FROM product_forms WHERE LOWER(code) = LOWER(#{code})")
  ProductForm getByCode(String code);
}
