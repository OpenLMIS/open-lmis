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

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Facility;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRequisitionMapper {

  @Select("SELECT max(name) from " +
    " facilities where facilities.id in ( select facilityid from requisitions where requisitions.id = #{rnrId} )")
  String getFacilityNameForRnrId(Long rnrId);

  @Select("SELECT max(name) from " +
    " programs where programs.id in ( select programid from requisitions where requisitions.id = #{rnrId} )")
  String getProgramNameForRnrId(Long rnrId);

  @Select("SELECT max(name) from " +
    " processing_periods where processing_periods.id in ( select periodid from requisitions where requisitions.id = #{rnrId} )")
  String getPeriodTextForRnrId(Long rnrId);

  @Select("SELECT * from " +
      " facilities where facilities.id in ( select facilityid from requisitions where requisitions.id = #{rnrId} )")
  @Results(value = {
      @Result(property = "geographicZone", column = "geographicZoneId", javaType = Long.class,
          one = @One(select = "org.openlmis.core.repository.mapper.GeographicZoneMapper.getWithParentById"))
  })
  Facility getFacilityForRnrId(Long rnrId);
}
