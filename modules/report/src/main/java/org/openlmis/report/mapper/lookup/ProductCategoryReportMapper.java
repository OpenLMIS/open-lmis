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
