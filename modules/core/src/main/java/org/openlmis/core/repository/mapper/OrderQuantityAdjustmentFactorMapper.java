package org.openlmis.core.repository.mapper;/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.OrderQuantityAdjustmentFactor;
import org.openlmis.core.domain.ProductCategory;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderQuantityAdjustmentFactorMapper {
    @Insert({"INSERT INTO order_quantity_adjustment_factors",
            "(id, name, description,displayOrder,basedOnFormula,createdBy" +
                    ",createdDate, modifiedBy, modifiedDate)",
            "VALUES",
            "(#{id}, #{name},#{description},#{displayOrder} #{basedOnFormula}, #{createdBy},NOW(), #{modifiedBy}, NOW()))"})
    @Options(useGeneratedKeys = true)
    public void insert(OrderQuantityAdjustmentFactor quantityAdjustmentFactor);

    @Select("SELECT * FROM order_quantity_adjustment_factors WHERE id = #{id}")
    public OrderQuantityAdjustmentFactor getById(Long id);

    @Update({"UPDATE order_quantity_adjustment_factors SET name = #{name}, modifiedBy = #{modifiedBy}," +
            " description=#{description} ,",
            "displayOrder = #{displayOrder}, modifiedDate =#{modifiedDate} where id = #{id}"})
    public void update(OrderQuantityAdjustmentFactor quantityAdjustmentFactor);



    @Select("SELECT * FROM order_quantity_adjustment_factors")
    public List<OrderQuantityAdjustmentFactor> getAll();
    @Delete({"DELETE order_quantity_adjustment_factors  where id = #{id}"})
   public  void delete(OrderQuantityAdjustmentFactor quantityAdjustmentFactor);
}
