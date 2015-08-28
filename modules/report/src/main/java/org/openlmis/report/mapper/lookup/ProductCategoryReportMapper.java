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

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.ProductCategory;
import org.openlmis.report.model.dto.ProductCategoryProductTree;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryReportMapper {

  @Select("SELECT id, name, code , displayorder" +
          "   FROM " +
          "       product_categories order by name")

  List<ProductCategory> getAll();


  @Select("SELECT distinct pc.id, pc.name, pc.code " +
      "   FROM " +
      "       product_categories pc " +
      "       join program_products pp on pp.productcategoryid = pc.id " +
      "   WHERE pp.programid = #{programId} and pp.active = true " +
      " order by name")

  List<ProductCategory> getForProgram(@Param("programId") int programId);


  @Select("SELECT distinct pc.id, pc.name, pc.code " +
    "   FROM " +
    "       product_categories pc " +
    "       join program_products pp on pp.productcategoryid = pc.id  " +
    "   WHERE pp.programid = #{programId} and pp.active = true " +
    " order by name")
  List<ProductCategory> getForProgramUsingProgramProductCategory(@Param("programId") int programId);

    @Select("SELECT pp.programId As program_id, pp.productcategoryid AS category_id, p.id AS product_id,  pc.name AS category, concat(p.primaryname ,' ', form.code ,' ', p.strength ,' ', du.code) as product, p.code as code  \n" +
            "FROM  " +
            "   products as p  " +
            "  join product_forms as form on form.id = p.formid  " +
            "  join dosage_units as du on du.id = p.dosageunitid  " +
            "  join program_products pp on pp.productId = p.id " +
            " join product_categories pc on   pc.id = pp.productcategoryid " +
            " where pp.programId = #{programId} and pp.active = true " +
            "    order by pp.programId, pp.productcategoryid, p.id")
    List<ProductCategoryProductTree> getProductCategoryProductByProgramId(@Param("programId") int programId);
}
