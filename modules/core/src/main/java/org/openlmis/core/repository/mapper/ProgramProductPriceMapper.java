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

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.springframework.stereotype.Repository;

/**
 * It maps ProgramProductPrice entity to corresponding representation in database. Allows operation to maintain
 * program product price history along with basic CRUD operations.
 */
@Repository
public interface ProgramProductPriceMapper {
  @Insert({"INSERT INTO program_product_price_history",
      "(programProductId, price, pricePerDosage, source, endDate, createdBy, modifiedBy, modifiedDate) VALUES",
      "(#{programProduct.id}, #{programProduct.currentPrice}, #{pricePerDosage}, #{source}, #{endDate}, #{createdBy}, #{modifiedBy}, #{modifiedDate})"})
  @Options(useGeneratedKeys = true)
  void insertNewCurrentPrice(ProgramProductPrice programProductPrice);

  @Update({"UPDATE program_product_price_history SET endDate = DEFAULT, modifiedBy = #{modifiedBy}, modifiedDate = DEFAULT where programProductId = #{programProduct.id} AND endDate IS NULL"})
  void closeLastActivePrice(ProgramProductPrice programProductPrice);

  @Select({"SELECT * FROM program_product_price_history where id = #{id}"})
  @Results({@Result(property = "programProduct.id", column = "programProductId"),
      @Result(property = "programProduct.currentPrice", column = "price")})
  ProgramProductPrice getById(Long id);

  @Select({"SELECT * FROM program_product_price_history WHERE programProductId = #{id} AND endDate IS NULL"})
  @Results({@Result(property = "programProduct.id", column = "programProductId"),
      @Result(property = "programProduct.currentPrice", column = "price")})
  ProgramProductPrice get(ProgramProduct programProduct);

}
