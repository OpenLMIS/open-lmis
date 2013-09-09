/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.Schedule;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduleReportMapper {

    @Select("SELECT id, name, description, code " +
            "   FROM " +
            "       processing_schedules order by name")
    List<Schedule> getAll();
}
