/*
 *This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.

  * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.

  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.

  * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.ProductPriceSchedule;
import org.openlmis.core.domain.PriceSchedule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPriceScheduleMapper {

    @Insert("INSERT INTO product_price_schedules( productId, priceScheduleId, price, createdBy, createdDate ) " +
            "    VALUES ( #{product.id}, #{priceSchedule.id}, #{price}, #{createdBy}, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true)
    Integer insert(ProductPriceSchedule productPriceSchedule);

    @Update("UPDATE product_price_schedules" +
            "   SET priceScheduleId=#{priceSchedule.id}, productId=#{product.id}, price=#{price}, modifiedDate = CURRENT_TIMESTAMP, modifiedBy = #{modifiedBy} WHERE id = #{id}")
    Integer update(ProductPriceSchedule productPriceSchedule);

    @Select("SELECT * FROM product_price_schedules WHERE productId = #{productId} and priceScheduleId = #{priceScheduleId}")
    ProductPriceSchedule getByProductCodePriceSchedule(@Param("productId") Long productId, @Param("priceScheduleId") Long priceScheduleId);

    @Select("select * from product_price_schedules where productId = #{id}")
    @Results(value = {
            @Result(property = "product.id", column = "productId"),
            @Result(
                    property = "priceSchedule", column = "priceScheduleId", javaType = PriceSchedule.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.PriceScheduleMapper.getById")),
            @Result(property = "price", column = "price")
    })
    List<ProductPriceSchedule> getByProductId(Long id);

    @Select("SELECT ps.* from facility_approved_products fap " +
            "INNER JOIN facilities f ON f.typeId = fap.facilityTypeId " +
            "INNER JOIN program_products pp ON pp.id = fap.programProductId " +
            "INNER JOIN products p ON p.id = pp.productId " +
            "INNER JOIN programs pgm ON pp.programId = pgm.id " +
            "LEFT JOIN product_price_schedules ps ON ps.productId = p.id AND f.priceScheduleId = ps.priceScheduleId " +
            "WHERE " +
            " pp.programId = #{programId} And " +
            " f.id = #{facilityId}  AND " +
            " pp.fullSupply = TRUE AND " +
            " p.active = TRUE AND " +
            " pp.active = TRUE")
    @Results(value = {
            @Result(property = "product.id", column = "productId"),
            @Result(property = "price", column = "price")
    })
    List<ProductPriceSchedule> getPriceScheduleFullSupplyFacilityApprovedProduct(@Param("programId") Long programId, @Param("facilityId") Long facilityId);
}
