package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.Schedule;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/12/13
 * Time: 2:39 AM
 */
@Repository
public interface ScheduleReportMapper {

    @Select("SELECT id, name, description, code " +
            "   FROM " +
            "       processing_schedules order by name")
    List<Schedule> getAll();
}
