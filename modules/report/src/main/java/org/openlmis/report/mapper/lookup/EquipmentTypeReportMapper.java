package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.EquipmentType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentTypeReportMapper {

    @Select("SELECT *" +
            "   FROM " +
            "       equipment_types order by name")
    List<EquipmentType> getEquipmentTypeList();
}
