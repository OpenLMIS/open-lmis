/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * e-lmis
 * Created by: Muhammad Ahmed
 * Date: 4/12/13
 * Time: 2:39 AM
 */
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
