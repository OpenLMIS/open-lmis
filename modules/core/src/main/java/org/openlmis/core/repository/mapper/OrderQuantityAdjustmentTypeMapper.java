/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.OrderQuantityAdjustmentType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderQuantityAdjustmentTypeMapper {
    @Insert({"INSERT INTO order_quantity_adjustment_types",
            "(name,description, displayOrder,createdBy" +
                    ",createdDate, modifiedBy, modifiedDate)",
            "VALUES",
            "( #{name},#{description},#{displayOrder} , #{createdBy},NOW(), #{modifiedBy}, NOW())"})
    @Options(useGeneratedKeys = true)
    public void insert(OrderQuantityAdjustmentType quantityAdjustmentType);

    @Select("SELECT * FROM order_quantity_adjustment_types WHERE id = #{id}")
    public OrderQuantityAdjustmentType getById(Long id);

    @Update({"UPDATE order_quantity_adjustment_types SET name = #{name}, modifiedBy = #{modifiedBy}," +
            "displayOrder = #{displayOrder},description=#{description}, modifiedDate =#{modifiedDate} where id = #{id}"})
    public void update(OrderQuantityAdjustmentType quantityAdjustmentType);



    @Select("SELECT * FROM order_quantity_adjustment_types")
    public List<OrderQuantityAdjustmentType> getAll();
    @Delete({"DELETE from order_quantity_adjustment_types  where id = #{id}"})
   public void delete(OrderQuantityAdjustmentType quantityAdjustmentType);
    @Select("SELECT * FROM order_quantity_adjustment_types" +
            " where LOWER(name) LIKE '%'|| LOWER(#{param}) ||'%'")
    List<OrderQuantityAdjustmentType> search(String param);
}
