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
import org.openlmis.core.domain.Product;
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
    " , regularFemale = #{regularFemale} " +
    " , outreachMale = #{outreachMale} " +
    " , outreachFemale = #{outreachFemale} " +
    " , campaignMale = #{campaignMale} " +
    " , campaignFemale = #{campaignFemale} " +
    " , modifiedBy = #{modifiedBy} " +
    " , modifiedDate = NOW()" +
    " WHERE id = #{id} ")
  void update(VaccineCoverageItem item);

  @Select("SELECT id, code, primaryName FROM products where id = #{id}")
  Product getProductDetails(Long id);

  @Select("SELECT * from vaccine_report_coverage_line_items WHERE id = #{id}")
  VaccineCoverageItem getById(@Param("id") Long id);

  @Select("SELECT * from vaccine_report_coverage_line_items WHERE reportId = #{reportId} and productId = #{productId} and doseId = #{doseId} order by displayOrder")
  @Results(value = {
    @Result(property = "productId", column = "productId"),
    @Result(property = "product", javaType = Product.class, column = "productId",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportCoverageMapper.getProductDetails")),
  })
  VaccineCoverageItem getCoverageByReportProductDosage(@Param("reportId") Long reportId, @Param("productId") Long productId, @Param("doseId") Long doseId);

  @Select("SELECT * from vaccine_report_coverage_line_items WHERE reportId = #{reportId} order by displayOrder")
  @Results(value = {
    @Result(property = "productId", column = "productId"),
    @Result(property = "product", javaType = Product.class, column = "productId",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportCoverageMapper.getProductDetails")),
  })
  List<VaccineCoverageItem> getLineItems(@Param("reportId") Long reportId);

}
