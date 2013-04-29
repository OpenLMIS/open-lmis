package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.RequisitionGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/12/13
 * Time: 2:39 AM
 */
@Repository
public interface RequisitionGroupReportMapper {

    @Select("SELECT id, name, code " +
            "   FROM " +
            "       requisition_groups")
    List<RequisitionGroup> getAll();
}
