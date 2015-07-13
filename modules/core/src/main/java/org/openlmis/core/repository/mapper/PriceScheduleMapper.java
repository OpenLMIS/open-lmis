/*
 *This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.

  * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.

  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.

  * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.PriceSchedule;
import org.openlmis.core.domain.PriceScheduleCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceScheduleMapper {

    @Insert("INSERT INTO price_schedule( productid, pricecatid, sale_price, createdBy,createdDate,modifiedDate,modifiedBy)\n " +
            "    VALUES ( #{product.id}, #{priceScheduleCategory.id}, #{salePrice}, #{createdBy}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, #{modifiedBy})")
    void insert(PriceSchedule priceSchedule);

    @Update("UPDATE price_schedule\n" +
            "   SET pricecatid=#{priceScheduleCategory.id}, productid=#{product.id}, sale_price=#{salePrice}, modifiedDate = CURRENT_TIMESTAMP, modifiedBy = #{modifiedBy} WHERE id = #{id}")
    void update(PriceSchedule priceSchedule);

    @Select("SELECT * FROM price_schedule WHERE productid = #{productId} and priceCatid = #{priceScheduleCategoryId}")
    PriceSchedule getByProductCodePriceScheduleCategory(@Param("productId") Long productId, @Param("priceScheduleCategoryId")  Long priceScheduleCategoryId);

    @Select("SELECT id FROM price_schedule_category WHERE LOWER(price_category) = LOWER(#{priceScheduleCategory})")
    Long getPriceCategoryIdByName(String priceScheduleCategory);

    @Select("select * from price_schedule_category where id = #{id}")
    PriceScheduleCategory getPriceScheduleCategoryById(Long id);

    @Select("select * from price_schedule where productid = #{id}")
    @Results(value = {
            @Result(property = "product.id", column = "productid"),
            @Result(
                    property = "priceScheduleCategory", column = "pricecatid", javaType = PriceScheduleCategory.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.PriceScheduleMapper.getPriceScheduleCategoryById")),
            @Result(property = "salePrice", column = "sale_price")
    })
    List<PriceSchedule> getByProductId(Long id);

    @Select("select * from price_schedule_category")
    List<PriceScheduleCategory> getPriceScheduleCategories();

    @Select("select * from price_schedule_category where price_category = #{code}")
    PriceScheduleCategory getPriceScheduleCategoryByCode(String code);

    @Select("SELECT ps.* from facility_approved_products fap " +
            "INNER JOIN facilities f ON f.typeId = fap.facilityTypeId " +
            "INNER JOIN program_products pp ON pp.id = fap.programProductId " +
            "INNER JOIN products p ON p.id = pp.productId " +
            "INNER JOIN programs pgm ON pp.programId = pgm.id " +
            "INNER JOIN price_schedule ps ON ps.productid = p.id AND f.pricecatid = ps.pricecatid " +
            "WHERE " +
            " pp.programId = #{programId} And " +
            " f.id = #{facilityId}  AND " +
            " pp.fullSupply = TRUE AND " +
            " p.active = TRUE AND " +
            " pp.active = TRUE")
    @Results(value = {
            @Result(property = "product.id", column = "productid"),
            @Result(property = "salePrice", column = "sale_price")
    })
    List<PriceSchedule> getPriceScheduleFullSupplyFacilityApprovedProduct(@Param("programId") Long programId, @Param("facilityId") Long facilityId);
}
