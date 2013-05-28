package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.AdjustmentType;
import org.openlmis.report.model.dto.ProductCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: Wolde
 * Date: 5/15/13
 * Time: 2:37 PM
 */
@Repository
public interface AdjustmentTypeReportMapper {
    @Select("SELECT name, description " +
            "   FROM " +
            "       losses_adjustments_types order by name")
    List<AdjustmentType> getAll();
}
