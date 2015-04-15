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
import org.openlmis.vaccine.domain.reports.VaccineCoverageItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineReportCoverageMapper {

  @Insert("INSERT into vaccine_report_coverage_line_items " +
    " (reportId, productId, doseId, displayName, displayOrder , trackMale, trackFemale, regularMale, regularFemale, outreachMale, outreachFemale, campaignMale, campaignFemale, createdBy, createdDate, modifiedBy, modifiedDate) " +
    " values " +
    " (#{reportId}, #{productId}, #{doseId}, #{displayName}, #{displayOrder}, #{trackMale}, #{trackFemale}, #{regularMale}, #{regularFemale}, #{outreachMale}, #{outreachFemale}, #{campaignMale}, #{campaignFemale}, #{createdBy}, NOW(), #{modifiedBy}, NOW())")
  @Options(useGeneratedKeys = true)
  Integer insert(VaccineCoverageItem item);

  @Update("UPDATE vaccine_report_coverage_line_items " +
    " SET " +
    " reportId = #{reportId} " +
    " , productId = #{productId} " +
    " , doseId  = #{doseId} " +
    " , displayName = #{displayName}  " +
    " , displayOrder = #{displayOrder}  " +
    " , trackMale = #{trackMale}  " +
    " , trackFemale = #{trackFemale}  " +
    " , regularMale = #{regularMale} " +
    " , outreachMale = #{outreachMale} " +
    " , outreachFemale = #{outreachFemale} " +
    " , campaignMale = #{campaignMale} " +
    " , campaignFemale = #{campaignFemale} " +
    " , modifiedBy = #{modifiedBy} " +
    " , modifiedDate = NOW()" +
    " WHERE id = #{id} ")
  void update(VaccineCoverageItem item);

  @Select("SELECT * from vaccine_report_coverage_line_items WHERE id = #{id}")
  VaccineCoverageItem getById(@Param("id") Long id);

  @Select("SELECT * from vaccine_report_coverage_line_items WHERE reportId = #{reportId} and productId = #{productId} and doseId = #{doseId} order by displayOrder")
  VaccineCoverageItem getCoverageByReportProductDosage(@Param("reportId") Long reportId, @Param("productId") Long productId, @Param("doseId") Long doseId);

  @Select("SELECT * from vaccine_report_coverage_line_items WHERE reportId = #{reportId} order by displayOrder")
  List<VaccineCoverageItem> getLineItems(@Param("reportId") Long reportId);

}
