package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.DosageUnit;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: Elias
 * Date: 6/30/13
 * Time: 2:37 PM
 */
@Repository
public interface DosageUnitReportMapper {

    @Select("SELECT id, code, displayorder " +
            "   FROM " +
            "       dosage_units order by id")
    List<DosageUnit> getAll();
}
