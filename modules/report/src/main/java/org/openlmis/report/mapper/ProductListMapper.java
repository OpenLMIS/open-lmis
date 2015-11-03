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

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.report.model.dto.ProductList;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductListMapper {

   // mahmed 07.11.2013 full product list
    @Select(value = "SELECT\n" +
            "products.id,\n" +
            "products.code,\n" +
            "products.fullname As fullName,\n" +
            "products.primaryName As primaryName,\n" +
            "products.strength,\n" +
            "products.dispensingUnit AS dispensingUnit,\n" +
            "dosage_units.code,\n" +
            "dosage_units.id AS dosageUnitId,\n" +
            "product_forms.code,\n" +
            "products.packSize,\n" +
            "products.packroundingthreshold AS packRoundingThreshold,\n" +
            "products.dosesperdispensingunit AS dosesPerDispensingUnit,\n" +
            "products.fullsupply AS fullSupply,\n" +
            "products.active AS active,\n" +
            "dosage_units.id AS dosageUnitId,\n" +
            "dosage_units.code AS dosageUnitCode,\n" +
            "product_forms.id AS formId,\n" +
            "product_forms.code AS formCode \n" +
            "FROM \n" +
            "products \n" +
            "LEFT OUTER JOIN product_forms ON  products.formid  = product_forms.id  \n" +
            "LEFT OUTER JOIN dosage_units ON  products.dosageunitid =  dosage_units.id  \n")
    @Results(value = {
        @Result(property = "id", column = "id"),
        @Result(property = "programs", javaType = List.class, column = "id",
            many = @Many(select = "getProgramsForProduct"))
    })
    List<ProductList> getList();

}
