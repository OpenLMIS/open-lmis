package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.ProductGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/12/13
 * Time: 2:39 AM
 */
@Repository
public interface ProductGroupReportMapper {

    @Select("SELECT * " +
            "   FROM " +
            "       product_groups order by name")
    List<ProductGroup> getAll();
}
