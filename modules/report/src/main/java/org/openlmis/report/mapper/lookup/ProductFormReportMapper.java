/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.ProductForm;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Elias Muluneh
 */
@Repository
public interface ProductFormReportMapper {

    @Select("SELECT * " +
            "   FROM " +
            "       product_forms order by displayorder")

    List<ProductForm> getAll();
}
