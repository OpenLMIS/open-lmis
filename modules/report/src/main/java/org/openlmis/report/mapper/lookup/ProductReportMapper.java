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

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
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
            "       products order by primaryname")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    List<org.openlmis.core.domain.Product> getFullProductList(@Param("RowBounds")RowBounds rowBounds);

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

    @Select("SELECT primaryname as name, * FROM products WHERE LOWER(code) = LOWER(#{code})")
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

  @Select("SELECT p.id, p.primaryname as name, p.code, pp.productcategoryid categoryid, \n" +
          "    CASE WHEN p.tracer = true THEN 'Indicator Product' ELSE 'Regular' END tracer\n" +
          "     \n" +
          "       FROM \n" +
          "           products as p \n" +
          "                 join product_forms as form on form.id = p.formid \n" +
          "                 join dosage_units as du on du.id = p.dosageunitid\n" +
          "             join program_products pp on p.id = pp.productId \n" +
          "             join programs pg on pg.id = pp.programId\n" +
          "         where pg.push = True  and pp.active = true \n" +
          "     order by name ")
  List<Product> getPushProgramProducts();

  @Select("SELECT p.id, (coalesce(p.primaryname,'') || ' ' || coalesce(form.code,'') || ' ' || coalesce(p.strength,'') || ' ' || coalesce(du.code,'')) as name, p.code, pp.productcategoryid as categoryid, " +
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

    @Select("SELECT p.id, (p.primaryname || ' ' || form.code || ' ' || p.strength || ' ' || du.code) as name, p.code, pp.productcategoryid categoryid, \n" +
            "CASE WHEN p.tracer = true THEN 'Indicator Product' ELSE 'Regular' END tracer\n" +
            "\n" +
            "FROM \n" +
            "products as p \n" +
            "join product_forms as form on form.id = p.formid \n" +
            "join dosage_units as du on du.id = p.dosageunitid\n" +
            "join program_products pp on p.id = pp.productId \n" +
            "join programs pr on pr.id = pp.programId\n" +
            "where LOWER(pr.code) = 'rmnch' and pp.active = true \n" +
            "order by name \n"
    )
    List<Product> getRmnchProducts();

}
