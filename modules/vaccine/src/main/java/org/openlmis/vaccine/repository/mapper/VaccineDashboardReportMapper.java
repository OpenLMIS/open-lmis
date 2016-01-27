/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.vaccine.domain.GeoZoneFacility;
import org.openlmis.vaccine.domain.GeoZoneTree;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineDashboardReportMapper {
   /* @Select("with geotree as (select distinct gz.* from geographic_zones gz \n" +
            "join (select vd.* from vw_districts vd join vw_user_districts vud on vud.district_id = vd.district_id where vud.user_id = #{userId}) sq\n" +
            "on sq.district_id = gz.id or sq.zone_id = gz.id or gz.id = sq.region_id or gz.id = sq.parent)\n" +
            "\n" +
            "select f.geographicZoneId geoZoneId,f.name facilityName,f.id facilityId,g.parentId, g.name geoName from geotree g\n" +
            "join facilities f on f.geographiczoneid = g.id")
    List<GeoZoneFacilityTree> getGeoZoneFacilityTreeForUser( @Param("userId") Long userId);*/

    @Select("select * from geographic_zones where parentId is null")
    GeoZoneTree getParentZoneTree();

    @Select("SELECT name as facilityName from facilities where geographiczoneid = #{geoZoneId}")
    List<GeoZoneFacility> getFacilitiesForGeoZone(int geoZoneId);

    @Select("select distinct gz.* from geographic_zones gz " +
            " join (select vd.* from vw_districts vd join vw_user_districts vud on vud.district_id = vd.district_id where vud.user_id = #{userId} and vd.zone_id=438) sq" +
            " on sq.district_id = gz.id or sq.zone_id = gz.id or gz.id = sq.region_id or gz.id = sq.parent")
    List<GeoZoneTree> getGeoZonesForUser( @Param("userId") Long userId);



}
