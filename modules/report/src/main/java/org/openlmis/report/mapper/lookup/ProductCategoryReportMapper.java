/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.ProductCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 5/4/13
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface ProductCategoryReportMapper {

    @Select("SELECT id, name, code " +
            "   FROM " +
            "       product_categories order by name")

    List<ProductCategory> getAll();
}
