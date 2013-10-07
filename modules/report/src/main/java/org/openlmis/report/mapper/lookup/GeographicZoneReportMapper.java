/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.GeographicZone;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeographicZoneReportMapper {

    @Select("SELECT g.id, g.code , g.name, p.name as parent" +
            "   FROM " +
            "       geographic_zones g left join geographic_zones p on g.parentid = p.id order by p.name, g.name")
    List<GeographicZone> getAll();
}
