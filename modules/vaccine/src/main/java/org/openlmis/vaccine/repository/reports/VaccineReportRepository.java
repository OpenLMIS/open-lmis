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

package org.openlmis.vaccine.repository.reports;

import org.openlmis.core.domain.GeographicZone;
import org.openlmis.vaccine.domain.reports.*;
import org.openlmis.vaccine.dto.ReportStatusDTO;
import org.openlmis.vaccine.repository.mapper.reports.VaccineReportMapper;
import org.openlmis.vaccine.service.reports.VaccineLineItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class VaccineReportRepository {

  @Autowired
  VaccineReportMapper mapper;

  @Autowired
  VaccineLineItemService lineItemService;


  public void insert(VaccineReport report){
    mapper.insert(report);
    saveDetails(report);
  }


  public void saveDetails(VaccineReport report){
    lineItemService.saveLogisticsLineItems(report.getLogisticsLineItems(), report.getId());
    lineItemService.saveDiseaseLineItems(report.getDiseaseLineItems(), report.getId());
    lineItemService.saveCoverageLineItems(report.getCoverageLineItems(),report.getId());
    lineItemService.saveColdChainLIneItems(report.getColdChainLineItems(), report.getId());
    lineItemService.saveVitaminLineItems(report.getVitaminSupplementationLineItems(), report.getId());
    lineItemService.saveAdverseEffectLineItems(report.getAdverseEffectLineItems(), report.getId());
    lineItemService.saveCampaignLineItems(report.getCampaignLineItems(), report.getId());
  }

  public void update(VaccineReport report, Long userId) {
    report.setModifiedBy(userId);
    mapper.update(report);
    saveDetails(report);
  }

  public VaccineReport getById(Long id){
    return mapper.getById(id);
  }

  public VaccineReport getByIdWithFullDetails(Long id){
    return mapper.getByIdWithFullDetails(id);
  }

  public VaccineReport getByProgramPeriod(Long facilityId, Long programId, Long periodId ){
    return mapper.getByPeriodFacilityProgram(facilityId, programId, periodId);
  }

  public VaccineReport getLastReport(Long facilityId, Long programId) {
    return mapper.getLastReport(facilityId, programId);
  }

  public Long getScheduleFor(Long facilityId, Long programId) {
    return mapper.getScheduleFor(facilityId, programId);
  }

  public List<ReportStatusDTO> getReportedPeriodsForFacility(Long facilityId, Long programId) {
    return mapper.getReportedPeriodsForFacility(facilityId, programId);
  }
  public Long getReportIdForFacilityAndPeriod(Long facilityId, Long periodId){
    return mapper.getReportIdForFacilityAndPeriod(facilityId, periodId);
  }
  public List<DiseaseLineItem> getDiseaseSurveillance(Long reportId){
    return mapper.getDiseaseSurveillance(reportId);
  }

  public List<DiseaseLineItem> getDiseaseSurveillanceAggregateReport(Long periodId, Long zoneId){
    return mapper.getDiseaseSurveillanceAggregateByGeoZone(periodId, zoneId);
  }

  public List<ColdChainLineItem> getColdChain(Long reportId){
    return mapper.getColdChain(reportId);
  }

  public List<ColdChainLineItem> getColdChainAggregateReport(Long periodId, Long zoneId){
    return mapper.getColdChainAggregateReport(periodId, zoneId);
  }

  public List<AdverseEffectLineItem> getAdverseEffectReport(Long reportId){
    return mapper.getAdverseEffectReport(reportId);
  }

  public List<AdverseEffectLineItem> getAdverseEffectAggregateReport(Long periodId, Long zoneId){
    return mapper.getAdverseEffectAggregateReport(periodId, zoneId);
  }

  public List<HashMap<String, Object>> getVaccineCoverageReport(Long reportId){
    return mapper.getVaccineCoverageReport(reportId);
  }

  public List<HashMap<String , Object>> getVaccineCoverageAggregateReport(Long periodId, Long zoneId){
    return mapper.getVaccineCoverageAggregateReportByGeoZone(periodId, zoneId);
  }

  public List<VaccineReport> getImmunizationSession(Long reportId){
    return mapper.getImmunizationSession(reportId);
  }

  public List<VaccineReport> getImmunizationSessionAggregate(Long periodId, Long zoneId){
    return mapper.getImmunizationSessionAggregate(periodId, zoneId);
  }

  public List<HashMap<String, Object>> getVaccinationReport(String productCategoryCode, Long reportId){
    return mapper.getVaccinationReport(productCategoryCode, reportId);
  }
  public List<HashMap<String, Object>> getVaccinationAggregateByGeoZoneReport(String productCategoryCode, Long periodId, Long zoneId){
    return mapper.getVaccinationAggregateByGeoZoneReport(productCategoryCode, periodId, zoneId);
  }

  public List<HashMap<String, Object>> getTargetPopulation(Long facilityId, Long periodId){
    return mapper.getTargetPopulation(facilityId, periodId);
  }

  public List<HashMap<String, Object>> getTargetPopulationAggregateByGeoZone(Long periodId, Long zoneId){
    return mapper.getTargetPopulationAggregateByGeoZone(periodId, zoneId);
  }
  public List<VitaminSupplementationLineItem> getVitaminSupplementationReport(Long reportId){
    return mapper.getVitaminSupplementationReport(reportId);
  }

  public List<VitaminSupplementationLineItem> getVitaminSupplementationAggregateReport(Long periodId, Long zoneId){
    return mapper.getVitaminSupplementationAggregateReport(periodId, zoneId);
  }

  public List<HashMap<String, Object>> vaccineUsageTrend(String facilityCode, String productCode){
    return mapper.vaccineUsageTrend(facilityCode, productCode);
  }
  public List<HashMap<String, Object>> vaccineUsageTrendByGeographicZone(Long periodId, Long zoneId, String productCode){
    return mapper.vaccineUsageTrendByGeographicZone(periodId, zoneId, productCode);
  }

  public List<HashMap<String, Object>> getAggregateDropOuts(Long periodId, Long zoneId){
    return mapper.getAggregateDropOuts(periodId, zoneId);
  }

  public List<HashMap<String, Object>> getDropOuts(Long reportId){
    return mapper.getDropOuts(reportId);
  }

  public GeographicZone getNationalZone() {
    return mapper.getNationalZone();
  }
}
