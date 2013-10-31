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
import org.openlmis.report.model.dto.ProductList;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReportMapper {

    @Select("SELECT id, primaryname as name, code, " +
            "CASE WHEN tracer = true THEN 'Indicator Product' ELSE 'Regular' END tracer" +
            " " +
            "   FROM " +
            "       products order by tracer,name")
    List<Product> getAll();

    @Select("SELECT * " +
            "   FROM " +
            "       products")
    List<ProductList> getFullProductList();

    @Select("SELECT id, primaryname as name, code, " +
            "CASE WHEN tracer = true THEN 'Indicator Product' ELSE 'Regular' END tracer  " +
            "FROM products " +
            "WHERE categoryid = #{categoryId} " +
            "order by tracer,name")
    List<Product> getProductListByCategory(@Param("categoryId") Integer categoryId);

    @Select("SELECT * FROM products WHERE code = #{code}")
    Product getProductByCode(String code);

  @Select("SELECT p.id, p.primaryName as name, p.code " +
      "     from products p " +
      "         join program_products pp on p.id = pp.productId " +
      "     where pp.programId = #{programId} and pp.active = true order by p.primaryName")
  List<Product> getProductsForProgram(Long programId);


}
