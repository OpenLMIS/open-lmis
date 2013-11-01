/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapperExtension  {

    @Select(value = "SELECT * FROM products where LOWER(name) like '%'|| LOWER(#{productSearchParam}) ||'%' OR LOWER(code) like '%'|| " +
            "LOWER(#{productSearchParam}) ||'%' ")
    List<Product> getProductWithSearchedName(String productSearchParam);

    // mahmed 07.11.2013 full product list
    @Select({"SELECT\n" +
            "products.id,\n" +
            "products.code,\n" +
            "products.fullname As fullName,\n" +
            "products.primaryname As primaryName,\n" +
            "product_categories.name AS type,\n" +
            "products.strength,\n" +
            "products.dispensingunit AS dispensingUnit,\n" +
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
            "product_forms.code AS formCode,\n" +
            "programs.id AS programId, \n" +
            "programs.name AS programName\n" +
            "FROM\n" +
            "products\n" +
            "INNER JOIN product_forms ON product_forms.id = products.formid\n" +
            "INNER JOIN dosage_units ON dosage_units.id = products.dosageunitid\n" +
            "INNER JOIN product_categories ON product_categories.id = products.categoryid\n" +
            "INNER JOIN program_products ON products.id = program_products.productid\n" +
            "INNER JOIN programs ON programs.id = program_products.programid"})
    @Results(value={
            @Result(property = "dosageUnit.id", column = "dosageUnitId"),
            @Result(property = "dosageUnit.code", column = "dosageUnitCode"),
            @Result(property = "category.id", column = "categoryId"),
            @Result(property = "form.id", column="formId"),
            @Result(property = "form.code", column = "formCode")

    })

    List<Product> getAllProducts_Ext();

 /*
    // mahmed 07.11.2013 full product list
    @Select({"SELECT\n" +
            "products.id,\n" +
            "products.code,\n" +
            "products.fullname As fullName,\n" +
            "products.primaryname As primaryName,\n" +
            "product_categories.name AS type,\n" +
            "products.strength,\n" +
            "products.dispensingunit AS dispensingUnit,\n" +
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
            "product_forms.code AS formCode,\n" +
            "programs.id AS programId, \n" +
            "programs.name AS programName\n" +
            "FROM\n" +
            "products\n" +
            "INNER JOIN product_forms ON product_forms.id = products.formid\n" +
            "INNER JOIN dosage_units ON dosage_units.id = products.dosageunitid\n" +
            "INNER JOIN product_categories ON product_categories.id = products.categoryid\n" +
            "INNER JOIN program_products ON products.id = program_products.productid\n" +
            "INNER JOIN programs ON programs.id = program_products.programid"})
    @Results(value={
            @Result(property = "dosageUnit.id", column = "dosageUnitId"),
            @Result(property = "dosageUnit.code", column = "dosageUnitCode"),
            @Result(property = "category.id", column = "categoryId"),
            @Result(property = "form.id", column="formId"),
            @Result(property = "form.code", column = "formCode"),
            @Result(property = "programName", column = "programName")
    })

    Product getProductById_Ext(Long productId);
  */
    // mahmed - 07.11.2013 - delete supply line
    @Update("UPDATE products SET  active=false where id = #{productId}")
    int deleteById_Ext(Long productId);

    // mahmed - 07.11.2013 - delete supply line
    @Update("UPDATE products SET  active=true where id = #{productId}")
    int restoreById_Ext(Long productId);

    @Select("SELECT * FROM products WHERE id=#{id}")
    @Results(value={
            @Result(property = "dosageUnit.id", column = "dosageUnitId"),
            @Result(property = "category.id", column = "categoryId"),
            @Result(property = "form.id", column="formId"),
    })
    Product getProductById(Long productId);

 }
