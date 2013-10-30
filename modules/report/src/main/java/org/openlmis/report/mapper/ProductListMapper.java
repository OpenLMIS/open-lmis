/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.report.model.dto.ProductList;
import org.openlmis.report.model.dto.Program;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductListMapper {

   // mahmed 07.11.2013 full product list
    @Select(value = "SELECT\n" +
            "products.id,\n" +
            "products.code,\n" +
            "products.fullname As fullName,\n" +
            "products.primaryname As primaryName,\n" +
            "product_categories.name AS type,\n" +
            "products.strength,\n" +
            "products.dispensingunit AS dispensingUnit,\n" +
            "dosage_units.code,\n" +
            "dosage_units.id AS dosageUnitId,\n" +
            "product_forms.code,\n" +
            "products.packSize,\n" +
            "products.packroundingthreshold AS packRoundingThreshold,\n" +
            "products.dosesperdispensingunit AS dosesPerDispensingUnit,\n" +
            "products.fullsupply AS fullSupply,\n" +
            "products.active AS active,\n" +
            "products.displayorder AS displayOrder,\n" +
            "dosage_units.id AS dosageUnitId,\n" +
            "dosage_units.code AS dosageUnitCode,\n" +
            "product_categories.id AS categoryId,\n" +
            "product_forms.id AS formId,\n" +
            "product_forms.code AS formCode \n" +
            "FROM \n" +
            "products \n" +
            "LEFT OUTER JOIN product_forms ON  products.formid  = product_forms.id  \n" +
            "LEFT OUTER JOIN dosage_units ON  products.dosageunitid =  dosage_units.id  \n" +
            "LEFT OUTER JOIN product_categories ON  products.categoryid = product_categories.id")
    @Results(value = {
        @Result(property = "id", column = "id"),
        @Result(property = "programs", javaType = List.class, column = "id",
            many = @Many(select = "getProgramsForProduct"))
    })
    List<ProductList> getList();

    @Select("select p.* from programs p " +
        "   join program_products pp on pp.programId = p.id and pp.productId = #{id} and pp.active = true")
    List<Program> getProgramsForProduct(Long id);

    // mahmed - 07.11.2013 - delete supply line
    @Update("UPDATE products SET  active=false where id = #{productId}")
    int deleteById(Long productId);

    // mahmed - 07.11.2013 - delete supply line
    @Update("UPDATE products SET  active=true where id = #{productId}")
    int restoreById(Long productId);

    @Select("SELECT * FROM products WHERE id=#{id}")
    Product getProductById(Long id);
}
