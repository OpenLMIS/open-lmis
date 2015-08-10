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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.vaccine.domain.reports.*;
import org.openlmis.vaccine.dto.ReportStatusDTO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface VaccineReportMapper {

  @Insert("INSERT into vaccine_reports (periodId, programId, facilityId, status, supervisoryNodeId, majorImmunizationActivities, fixedImmunizationSessions, outreachImmunizationSessions,outreachImmunizationSessionsCanceled, createdBy, createdDate, modifiedBy, modifiedDate) " +
    " values (#{periodId}, #{programId}, #{facilityId}, #{status}, #{supervisoryNodeId}, #{majorImmunizationActivities}, #{fixedImmunizationSessions}, #{outreachImmunizationSessions}, #{outreachImmunizationSessionsCanceled}, #{createdBy}, NOW(), #{modifiedBy}, NOW() )")
  @Options(useGeneratedKeys = true)
  Integer insert(VaccineReport report);

  @Select("SELECT * from vaccine_reports where id = #{id}")
  VaccineReport getById(@Param("id") Long id);

  @Select("SELECT * from vaccine_reports where facilityId = #{facilityId} and programId = #{programId} and periodId = #{periodId}")
  VaccineReport getByPeriodFacilityProgram(@Param("facilityId") Long facilityId, @Param("periodId") Long periodId, @Param("programId") Long programId);

  @Select("SELECT * from vaccine_reports where id = #{id}")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "facilityId", column = "facilityId"),
    @Result(property = "periodId", column = "periodId"),
    @Result(property = "programId", column = "programId"),
    @Result(property = "logisticsLineItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportLogisticsLineItemMapper.getLineItems")),
    @Result(property = "coverageLineItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportCoverageMapper.getLineItems")),
    @Result(property = "adverseEffectLineItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportAdverseEffectMapper.getLineItems")),
    @Result(property = "columnTemplate", javaType = List.class, column = "programId",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.VaccineColumnTemplateMapper.getForProgram")),
    @Result(property = "coldChainLineItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportColdChainMapper.getLineItems")),
    @Result(property = "campaignLineItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportCampaignLineItemMapper.getLineItems")),
    @Result(property = "diseaseLineItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VaccineReportDiseaseLineItemMapper.getLineItems")),
    @Result(property = "vitaminSupplementationLineItems", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.vaccine.repository.mapper.reports.VitaminSupplementationLineItemMapper.getLineItems")),
    @Result(property = "period", javaType = ProcessingPeriod.class, column = "periodId",
      many = @Many(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById")),
    @Result(property = "facility", javaType = Facility.class, column = "facilityId",
      many = @Many(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
  })
  VaccineReport getByIdWithFullDetails(@Param("id") Long id);

  @Update("UPDATE vaccine_reports" +
      " set" +
      " periodId = #{periodId}, " +
      " programId = #{programId}, " +
      " facilityId = #{facilityId}, " +
      " status = #{status}, " +
      " supervisoryNodeId = #{supervisoryNodeId}, " +
      " majorImmunizationActivities = #{majorImmunizationActivities}, " +
      " fixedImmunizationSessions = #{fixedImmunizationSessions}, " +
      " outreachImmunizationSessions = #{outreachImmunizationSessions}, " +
      " outreachImmunizationSessionsCanceled = #{outreachImmunizationSessionsCanceled}, " +
      " modifiedBy = #{modifiedBy}, " +
      " modifiedDate = NOW() " +
    "where id = #{id}")
  void update(VaccineReport report);

  @Select("select max(s.scheduleId) id from requisition_group_program_schedules s " +
    " join requisition_group_members m " +
    "     on m.requisitionGroupId = s.requisitionGroupId " +
    " where " +
    "   s.programId = #{programId} " +
    "   and m.facilityId = #{facilityId} ")
  Long getScheduleFor(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

  @Select("select * from vaccine_reports " +
    "   where " +
    "     facilityId = #{facilityId} and programId = #{programId} order by id desc limit 1")
  VaccineReport getLastReport(@Param("facilityId") Long facilityId, @Param("programId") Long programId);


  @Select("select r.id, p.name as periodName, r.facilityId, r.status, r.programId " +
    " from vaccine_reports r " +
    "   join processing_periods p on p.id = r.periodId " +
    " where r.facilityId = #{facilityId} and r.programId = #{programId}" +
    " order by p.startDate desc")
  List<ReportStatusDTO> getReportedPeriodsForFacility(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

  @Select("Select id from vaccine_reports where facilityid = #{facilityId} and periodid = #{periodId}")
  Long getReportIdForFacilityAndPeriod(@Param("facilityId")Long facilityId, @Param("periodId")Long periodId);

  @Select("select COALESCE(cases, 0) as cases, COALESCE(death, 0) as death, COALESCE(cum_cases, 0) as cumulative, disease_name as diseaseName \n" +
          "from vw_vaccine_disease_surveillance \n" +
          "where report_id = #{reportId}")
  List<DiseaseLineItem> getDiseaseSurveillance(@Param("reportId")Long reportId);

  @Select("select equipment_type_name as equipmentName, model, minTemp, maxTemp, minEpisodeTemp, maxEpisodeTemp, energy_source as energySource from vw_vaccine_cold_chain \n" +
          "where report_id = #{reportId}")
  List<ColdChainLineItem> getColdChain(@Param("reportId")Long reportId);

  @Select("select product_name as productName, aefi_expiry_date as expiry, aefi_case as cases, aefi_batch as batch, 'missing'::text as manufacturer, false as isInvestigated from vw_vaccine_iefi \n" +
          "where report_id = #{reportId}")
  List<AdverseEffectLineItem> getAdverseEffectReport(@Param("reportId")Long reportId);

  @Select("select product_name,display_name, COALESCE(within_male, 0) within_male, COALESCE(within_female,0) within_female, COALESCE(within_total,0) within_total, COALESCE(within_coverage, 0) within_coverage, \n" +
          "COALESCE(outside_male, 0) outside_male, COALESCE(outside_female,0) outside_female, COALESCE(outside_total, 0) outside_total,\n" +
          "COALESCE(within_outside_total, 0) within_outside_total, COALESCE(within_outside_coverage,0) within_outside_coverage,\n" +
          " COALESCE(cum_within_total,0) cum_within_total, COALESCE(cum_within_coverage,0) cum_within_coverage,\n" +
          "  COALESCE(cum_outside_total,0) cum_outside_total, COALESCE(cum_within_outside_total,0) cum_within_outside_total,\n" +
          "   COALESCE(cum_within_outside_coverage ,0) cum_within_outside_coverage\n" +
          "from vw_vaccine_coverage \n" +
          "where report_id = #{reportId}")
  List<HashMap<String, Object>> getVaccineCoverageReport(@Param("reportId")Long reportId);

  @Select("SELECT COALESCE(fixedimmunizationsessions, 0) fixedimmunizationsessions, COALESCE(outreachimmunizationsessions, 0) outreachimmunizationsessions, COALESCE(outreachimmunizationsessionscanceled, 0) outreachimmunizationsessionscanceled FROM vaccine_reports WHERE id = #{reportId} ")
  List<VaccineReport> getImmunizationSession(@Param("reportId")Long reportId);

  @Select("select * from vw_vaccine_stock_status where product_category_code = (select value from configuration_settings where key = #{productCategoryCode}) and report_id = #{reportId}")
  List<HashMap<String, Object>> getVaccinationReport(@Param("productCategoryCode") String categoryCode, @Param("reportId")Long reportId);

  @Select("select * from vw_vaccine_target_population\n" +
          "where facility_id = #{facilityId} and year =  (select date_part('year'::text, processing_periods.startdate) from processing_periods where id = #{periodId})\n")
  List<HashMap<String, Object>> getTargetPopulation(@Param("facilityId") Long facilityId, @Param("periodId") Long periodId);

  @Select("Select age_group AS ageGroup, vitamin_name AS vitaminName, male_value AS maleValue, female_value AS femaleValue from vw_vaccine_vitamin_supplementation where report_id = #{reportId}")
  List<VitaminSupplementationLineItem> getVitaminSupplementationReport(@Param("reportId") Long reportId);

  @Select("select COALESCE(fr.quantity_issued, 0) quantity_issued, COALESCE(fr.closing_balance, 0) closing_balance, pp.name period_name \n" +
          "from fn_vaccine_facility_n_rnrs('Vaccine',#{facilityCode}, #{productCode},4) fr \n" +
          "JOIN processing_periods pp ON pp.id = fr.period_id\n" +
          "order by pp.id asc;")
  List<HashMap<String, Object>> vaccineUsageTrend(@Param("facilityCode")String facilityCode, @Param("productCode")String productCode);

  @Select("SELECT product_code,\n" +
          "MAX(product_name) product_name,\n" +
          "sum(opening_balanace) opening_balance,\n" +
          "sum(quantity_received) quantity_received,\n" +
          "sum(quantity_issued) quantity_issued,\n" +
          "sum(quantity_vvm_alerted) quantity_vvm_alerted,\n" +
          "sum(quantity_freezed) quantity_freezed,\n" +
          "sum(quantity_expired) quantity_expired,\n" +
          "sum(quantity_discarded_unopened) quantity_discarded_unopened,\n" +
          "sum(quantity_discarded_opened) quantity_discarded_opened,\n" +
          "sum(quantity_wasted_other) quantity_wasted_other,\n" +
          "sum(closing_balance) closing_balance,\n" +
          "sum(days_stocked_out) days_stocked_out,\n" +
          "'' AS reason_for_discarding,\n" +
          "sum(children_immunized) children_immunized,\n" +
          "sum(pregnant_women_immunized) pregnant_women_immunized,\n" +
          "sum(usage_rate) usage_rate,\n" +
          "sum(wastage_rate) wastage_rate \n" +
          "from vw_vaccine_stock_status \n" +
          "INNER JOIN vw_districts vd ON vd.district_id = geographic_zone_id\n" +
          "where period_id = #{periodId} and (vd.parent = #{zoneId} or vd.district_id = #{zoneId} or vd.region_id = #{zoneId} or vd.zone_id = #{zoneId} )\n" +
          "group by product_code")
  List<HashMap<String, Object>> getVaccinationAggregateByGeoZoneReport(@Param("periodId")Long periodId, @Param("zoneId") Long zoneId);
  @Select("select COALESCE(fr.quantity_issued, 0) quantity_issued, COALESCE(fr.closing_balance, 0) closing_balance, pp.name period_name \n" +
          "from fn_vaccine_geozone_n_rnrs('Vaccine', #{periodId}::integer ,#{zoneId}::integer, #{productCode},4) fr\n" +
          "JOIN processing_periods pp ON pp.id = fr.period_id\n" +
          "order by pp.id asc")
  List<HashMap<String, Object>>vaccineUsageTrendByGeographicZone(@Param("periodId") Long periodId, @Param("zoneId") Long zoneId, @Param("productCode") String productCode);


}
