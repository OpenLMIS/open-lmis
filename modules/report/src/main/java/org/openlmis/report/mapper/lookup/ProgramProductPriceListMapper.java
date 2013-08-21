/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
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
