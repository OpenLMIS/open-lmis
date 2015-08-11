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
import org.openlmis.core.domain.OrderQuantityAdjustmentFactor;
import org.openlmis.core.domain.OrderQuantityAdjustmentProduct;
import org.openlmis.core.domain.OrderQuantityAdjustmentType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderQuantityAdjustmentProductMapper {
    @Select("Select * from order_quantity_adjustment_products")
    @Results(value = {
            @Result(property = "product.id", column = "productId"),
            @Result(property = "facility.id", column = "facilityId")
    })
    public List<OrderQuantityAdjustmentProduct> getAll();

    @Insert("INSERT INTO order_quantity_adjustment_products(facilityid, productid, typeid, factorid, startdate, enddate, \n" +
            "            minmonthsofstock, maxmonthsofstock, formula, createdby, createddate, modifiedby, modifieddate, description)\n" +
            "    VALUES (#{facility.id}, #{product.id}, #{adjustmentType.id}, #{adjustmentFactor.id}, #{startDate}, #{endDate},\n" +
            "     #{minMOS}, #{maxMOS}, #{formula}, #{createdBy}, COALESCE(#{createdDate}, CURRENT_TIMESTAMP), #{modifiedBy}, COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP),#{description});\n")
    @Options(useGeneratedKeys = true)
    public void insert(OrderQuantityAdjustmentProduct adjustmentProduct);

    @Select("Select * from order_quantity_adjustment_products where productId = #{productId} and facilityId = #{facilityId}")
    @Results(value = {
            @Result(property = "product.id", column = "productId"),
            @Result(property = "facility.id", column = "facilityId"),
            @Result(property = "minMOS", column = "minmonthsofstock"),
            @Result(property = "maxMOS", column = "maxmonthsofstock"),
            @Result(property = "adjustmentType", column = "typeId", javaType = OrderQuantityAdjustmentType.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.OrderQuantityAdjustmentTypeMapper.getById")),
            @Result(property = "adjustmentFactor", column = "factorId", javaType = OrderQuantityAdjustmentFactor.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.OrderQuantityAdjustmentFactorMapper.getById")),
    })
    public OrderQuantityAdjustmentProduct getByProductAndFacility(@Param("productId") Long productId, @Param("facilityId") Long facilityId);
}
