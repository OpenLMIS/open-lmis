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

package org.openlmis.vaccine.repository.mapper.reports;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.reports.DiseaseLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineReportDiseaseLineItemMapper {

  @Insert("INSERT into vaccine_report_disease_line_items " +
    " (reportId, diseaseId, diseaseName, displayOrder, cases, death, cumulative, createdBy, createdDate, modifiedBy, modifiedDate) " +
    " values" +
    " (#{reportId}, #{diseaseId}, #{diseaseName}, #{displayOrder}, #{cases}, #{death}, #{cumulative}, #{createdBy}, NOW(), #{modifiedBy}, NOW())")
  @Options(useGeneratedKeys = true)
  void insert(DiseaseLineItem lineItem);

  @Update("UPDATE vaccine_report_disease_line_items " +
    " SET " +
    " reportId = #{reportId} " +
    " , diseaseId = #{diseaseId} " +
    " , diseaseName = #{diseaseName} " +
    " , displayOrder = #{displayOrder} " +
    " , cases = #{cases} " +
    " , death = #{death} " +
    " , cumulative = #{cumulative} " +
    " , modifiedBy = #{modifiedBy} " +
    " , modifiedDate = NOW()" +
    " WHERE id = #{id}")
  void update(DiseaseLineItem lineItem);

  @Select("select li.* " +
              ", (select sum(cases) from vaccine_report_disease_line_items l " +
                    "join vaccine_reports as r on r.id = l.reportId " +
                    "join processing_periods as pp on pp.id = r.periodid  " +
                    " where " +
                    " extract(year from pp.startDate) = extract(year from pd.startDate) " +
                    " and pp.startDate < pd.startDate " +
                    " and r.facilityId = rp.facilityId    " +
                    " and l.diseaseId = li.diseaseId " +
                    ") as calculatedCumulativeCases " +
              ", (select sum(death) from vaccine_report_disease_line_items l " +
                    "join vaccine_reports as r on r.id = l.reportId " +
                    "join processing_periods as pp on pp.id = r.periodid  " +
                    " where " +
                    " extract(year from pp.startdate) = extract(year from pd.startDate) " +
                    " and pp.startDate < pd.startDate " +
                    " and r.facilityId = rp.facilityId  " +
                    " and l.diseaseId = li.diseaseId " +
                    ") as calculatedCumulativeDeaths " +
          " from " +
          " vaccine_report_disease_line_items li " +
          " join vaccine_reports rp " +
          "   on rp.id = li.reportid " +
          " join processing_periods pd " +
          "   on pd.id = rp.periodid " +
    " WHERE li.reportId = #{reportId} " +
    " order by id")
  List<DiseaseLineItem> getLineItems(@Param("reportId")Long reportId);
}
