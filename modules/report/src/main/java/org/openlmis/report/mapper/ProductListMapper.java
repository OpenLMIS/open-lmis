/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
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

/**
 * e-lmis
 * Created by: Muhammad Ahmed
 * Date: 4/12/13
 * Time: 2:39 AM
 */
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
            "INNER JOIN product_forms ON product_forms.id = products.formid \n" +
            "INNER JOIN dosage_units ON dosage_units.id = products.dosageunitid \n" +
            "INNER JOIN product_categories ON product_categories.id = products.categoryid")
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
