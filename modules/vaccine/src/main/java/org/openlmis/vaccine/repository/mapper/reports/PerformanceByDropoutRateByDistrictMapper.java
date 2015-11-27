/*
 *
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *  Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openlmis.vaccine.repository.mapper.reports;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.openlmis.core.domain.Product;
import org.openlmis.vaccine.domain.reports.DropoutProduct;
import org.openlmis.vaccine.domain.reports.PerformanceByDropoutRateByDistrict;
import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;
import org.openlmis.vaccine.repository.mapper.reports.builder.PerformanceByDropoutRateQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PerformanceByDropoutRateByDistrictMapper {
    @SelectProvider(type=PerformanceByDropoutRateQueryBuilder.class, method="getByDistrictQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<PerformanceByDropoutRateByDistrict> loadPerformanceByDropoutRateDistrictReports(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );
    @SelectProvider(type=PerformanceByDropoutRateQueryBuilder.class, method="getByFacilityQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<PerformanceByDropoutRateByDistrict> loadPerformanceByDropoutRateFacilityReports(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );
    @SelectProvider(type=PerformanceByDropoutRateQueryBuilder.class, method="getByRegionQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<PerformanceByDropoutRateByDistrict> loadPerformanceByDropoutRateRegionReports(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );
    @Select("select count(*) from geographic_zones  gz " +
            "join geographic_levels gl on gz.levelid= gl.id " +
            " where gl.code='dist' and  gz.id= #{zoneId}")
    public int isDistrictLevel(@Param("zoneId")Long goegraphicZoneId);
    @Select("select " +
            "   case when code = 'V001' then 'BCG - MR' else 'DTP-HepB-Hib1/DTP-HepB-Hib3' end dropout , id " +
            "    from products where code in ('V001','V010')")
    public List<DropoutProduct> loadDropoutProductList();
}
