/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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
                    ") as calculatedCumulativeCases " +
              ", (select sum(death) from vaccine_report_disease_line_items l " +
                    "join vaccine_reports as r on r.id = l.reportId " +
                    "join processing_periods as pp on pp.id = r.periodid  " +
                    " where " +
                    " extract(year from pp.startdate) = extract(year from pd.startDate) " +
                    " and pp.startDate < pd.startDate " +
                    " and r.facilityId = rp.facilityId    " +
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
