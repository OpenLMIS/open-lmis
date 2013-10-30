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

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.ProgramProductPriceList;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * e-lmis
 * Created by: Muhammad Ahmed
 * Date: 4/12/13
 * Time: 2:39 AM
 */
@Repository
public interface ProgramProductPriceListMapper {

    @Select("SELECT\n" +
            "program_product_price_history.id,\n" +
            "program_products.programid,\n" +
            "program_products.productid,\n" +
            "program_product_price_history.programproductid,\n" +
            "programs.name AS programname,\n" +
            "program_product_price_history.price AS priceperpack,\n" +
            "program_product_price_history.priceperdosage,\n" +
            "program_product_price_history.startdate,\n" +
            "program_product_price_history.enddate,\n" +
            "program_product_price_history.source\n" +
            "FROM\n" +
            "programs\n" +
            "INNER JOIN program_products ON programs.id = program_products.programid\n" +
            "INNER JOIN program_product_price_history ON program_products.id = program_product_price_history.programproductid " +
            "WHERE productid = #{productId} Order By program_product_price_history.createddate")

    List<ProgramProductPriceList> getByProductId(Long productId);

    @Select("SELECT\n" +
            "program_product_price_history.id,\n" +
            "program_products.programid,\n" +
            "program_products.productid,\n" +
            "program_product_price_history.programproductid,\n" +
            "programs.name AS programname,\n" +
            "program_product_price_history.price AS priceperpack,\n" +
            "program_product_price_history.priceperdosage,\n" +
            "program_product_price_history.startdate,\n" +
            "program_product_price_history.enddate,\n" +
            "program_product_price_history.source\n" +
            "FROM\n" +
            "programs\n" +
            "INNER JOIN program_products ON programs.id = program_products.programid\n" +
            "INNER JOIN program_product_price_history ON program_products.id = program_product_price_history.programproductid " +
            "Order By program_product_price_history.createddate DESC")

    List<ProgramProductPriceList> getAllPrices();
}
