/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReportMapper {

    @Select("SELECT p.id, (p.primaryname || ' ' || form.code || ' ' || p.strength || ' ' || du.code) as name, p.code,  " +
            "CASE WHEN p.tracer = true THEN 'Indicator Product' ELSE 'Regular' END tracer" +
            " " +
            "   FROM " +
            "       products as p " +
      "             join product_forms as form on form.id = p.formid " +
      "             join dosage_units as du on du.id = p.dosageunitid" +
      "          order by p.tracer, name")
    List<Product> getAll();

    @Select("SELECT * " +
            "   FROM " +
            "       products")
    List<org.openlmis.core.domain.Product> getFullProductList();

  @Select("SELECT p.id, (p.primaryname || ' ' || form.code || ' ' || p.strength || ' ' || du.code) as name, p.code, pp.productcategoryid as categoryid, " +
    "CASE WHEN p.tracer = true THEN 'Indicator Product' ELSE 'Regular' END tracer" +
    " " +
    "   FROM " +
    "       products as p " +
    "             join product_forms as form on form.id = p.formid " +
    "             join dosage_units as du on du.id = p.dosageunitid " +
      "           join program_products pp on pp.productId = p.id " +
            "WHERE pp.programId = #{programId} and pp.productcategoryid = #{categoryId} " +
            "order by p.tracer, name")
    List<Product> getProductListByCategory(@Param("programId") Integer programId ,@Param("categoryId") Integer categoryId);

    @Select("SELECT * FROM products WHERE LOWER(code) = LOWER(#{code})")
    Product getProductByCode( String code);

  @Select("SELECT p.id, (p.primaryname || ' ' || form.code || ' ' || p.strength || ' ' || du.code) as name, p.code, pp.productcategoryid categoryid, " +
    "CASE WHEN p.tracer = true THEN 'Indicator Product' ELSE 'Regular' END tracer" +
    " " +
    "   FROM " +
    "       products as p " +
    "             join product_forms as form on form.id = p.formid " +
    "             join dosage_units as du on du.id = p.dosageunitid" +
    "         join program_products pp on p.id = pp.productId " +
    "     where pp.programId = #{programId} and pp.active = true " +
    " order by name "
  )
  List<Product> getProductsForProgram(Long programId);

  @Select("SELECT p.id, (p.primaryname || ' ' || form.code || ' ' || p.strength || ' ' || du.code) as name, p.code, pp.productcategoryid as categoryid, " +
    "CASE WHEN p.tracer = true THEN 'Indicator Product' ELSE 'Regular' END tracer" +
    " " +
    "   FROM " +
    "       products as p " +
    "             join product_forms as form on form.id = p.formid " +
    "             join dosage_units as du on du.id = p.dosageunitid" +
    "         join program_products pp on p.id = pp.productId " +
    "     where pp.programId = #{programId} and pp.active = true " +
    " order by name "
  )
  List<Product> getProductsForProgramPickCategoryFromProgramProduct(Long programId);

  @Select("SELECT p.id, (p.primaryname || ' ' || form.code || ' ' || p.strength || ' ' || du.code) as name, p.code, pp.categoryid, " +
    "CASE WHEN p.tracer = true THEN 'Indicator Product' ELSE 'Regular' END tracer" +
    " " +
    "   FROM " +
    "       products as p " +
    "             join product_forms as form on form.id = p.formid " +
    "             join dosage_units as du on du.id = p.dosageunitid" +
    "     where p.id = ANY( #{productIds}::INT[] )   " +
    " order by name "
  )
  List<Product> getSelectedProducts(@Param("productIds") String productIds);

}
