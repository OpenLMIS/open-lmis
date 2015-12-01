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

package org.openlmis.vaccine.service.reports;

import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.vaccine.domain.VaccineDisease;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.openlmis.vaccine.domain.Vitamin;
import org.openlmis.vaccine.domain.VitaminSupplementationAgeGroup;
import org.openlmis.vaccine.domain.reports.*;
import org.openlmis.vaccine.dto.ReportStatusDTO;
import org.openlmis.vaccine.repository.VitaminRepository;
import org.openlmis.vaccine.repository.VitaminSupplementationAgeGroupRepository;
import org.openlmis.vaccine.repository.reports.VaccineReportColdChainRepository;
import org.openlmis.vaccine.repository.reports.VaccineReportRepository;
import org.openlmis.vaccine.repository.reports.VaccineReportStatusChangeRepository;
import org.openlmis.vaccine.service.DiseaseService;
import org.openlmis.vaccine.service.VaccineIvdTabVisibilityService;
import org.openlmis.vaccine.service.VaccineProductDoseService;
import org.openlmis.demographics.service.AnnualFacilityDemographicEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.openlmis.vaccine.utils.ListUtil.emptyIfNull;

@Service
@NoArgsConstructor
public class VaccineReportService {

  public static final String VACCINE_REPORT_VACCINE_CATEGORY_CODE = "VACCINE_REPORT_VACCINE_CATEGORY_CODE";
  public static final String VACCINE_REPORT_VITAMINS_CATEGORY_CODE = "VACCINE_REPORT_VITAMINS_CATEGORY_CODE";
  public static final String VACCINE_REPORT_SYRINGES_CATEGORY_CODE = "VACCINE_REPORT_SYRINGES_CATEGORY_CODE";


  @Autowired
  VaccineReportRepository repository;

  @Autowired
  ProgramProductService programProductService;

  @Autowired
  DiseaseService diseaseService;

  @Autowired
  ProcessingPeriodRepository periodService;

  @Autowired
  VaccineProductDoseService productDoseService;

  @Autowired
  VaccineReportColdChainRepository coldChainRepository;

  @Autowired
  VitaminRepository vitaminRepository;

  @Autowired
  VitaminSupplementationAgeGroupRepository ageGroupRepository;

  @Autowired
  ProgramService programService;

  @Autowired
  VaccineIvdTabVisibilityService tabVisibilityService;

  @Autowired
  VaccineReportStatusChangeRepository reportStatusChangeRepository;

  @Autowired
  AnnualFacilityDemographicEstimateService annualFacilityDemographicEstimateService;

  @Transactional
  public VaccineReport initialize(Long facilityId, Long programId, Long periodId, Long userId) {
    VaccineReport report = repository.getByProgramPeriod(facilityId, programId, periodId);
    if (report != null) {
      return report;
    }
    report = createNewVaccineReport(facilityId, programId, periodId);
    repository.insert(report);
    ReportStatusChange change = new ReportStatusChange(report, ReportStatus.DRAFT, userId);
    reportStatusChangeRepository.insert(change);
    return report;
  }

  @Transactional
  public void save(VaccineReport report, Long userId) {
    repository.update(report, userId);
  }

  @Transactional
  public void submit(VaccineReport report, Long userId) {
    report.setStatus(ReportStatus.SUBMITTED);
    repository.update(report, userId);
    ReportStatusChange change = new ReportStatusChange(report, ReportStatus.SUBMITTED, userId);
    reportStatusChangeRepository.insert(change);
  }

  private VaccineReport createNewVaccineReport(Long facilityId, Long programId, Long periodId) {
    VaccineReport report;
    List<ProgramProduct> programProducts = programProductService.getActiveByProgram(programId);
    List<VaccineDisease> diseases = diseaseService.getAll();
    List<VaccineProductDose> dosesToCover = productDoseService.getForProgram(programId);
    List<ColdChainLineItem> coldChainLineItems = coldChainRepository.getNewEquipmentLineItems(programId, facilityId);
    List<Vitamin> vitamins = vitaminRepository.getAll();
    List<VitaminSupplementationAgeGroup> ageGroups = ageGroupRepository.getAll();

    VaccineReport previousReport = this.getPreviousReport(facilityId, programId, periodId);

    report = new VaccineReport();
    report.setFacilityId(facilityId);
    report.setProgramId(programId);
    report.setPeriodId(periodId);
    report.setStatus(ReportStatus.DRAFT);

    // 1. copy the products list and initiate the logistics tab.
    report.initializeLogisticsLineItems(programProducts, previousReport);

    // 2. copy the product + dosage settings and initiate the coverage tab.
    report.initializeCoverageLineItems(dosesToCover);

    // 3. copy the disease list and initiate the disease tab.
    report.initializeDiseaseLineItems(diseases);

    // 4. initialize the cold chain line items.
    report.initializeColdChainLineItems(coldChainLineItems);

    report.initializeVitaminLineItems(vitamins, ageGroups);
    return report;
  }

  private VaccineReport getPreviousReport(Long facilityId, Long programId, Long periodId) {
    Long reportId = repository.findLastReportBeforePeriod(facilityId, programId, periodId);
    return repository.getByIdWithFullDetails(reportId);
  }

  public List<ReportStatusDTO> getReportedPeriodsFor(Long facilityId, Long programId) {
    return repository.getReportedPeriodsForFacility(facilityId, programId);
  }

  public List<ReportStatusDTO> getPeriodsFor(Long facilityId, Long programId, Date endDate) {
    Date startDate = programService.getProgramStartDate(facilityId, programId);

    // find out which schedule this facility is in?
    Long scheduleId = repository.getScheduleFor(facilityId, programId);
    VaccineReport lastRequest = repository.getLastReport(facilityId, programId);

    if (lastRequest != null) {
      lastRequest.setPeriod(periodService.getById(lastRequest.getPeriodId()));
      startDate = lastRequest.getPeriod().getStartDate();
    }

    List<ReportStatusDTO> results = new ArrayList<>();
    // find all periods that are after this period, and before today.

    List<ProcessingPeriod> periods = periodService.getAllPeriodsForDateRange(scheduleId, startDate, endDate);
    if (lastRequest != null && lastRequest.getStatus().equals(ReportStatus.DRAFT)) {
      ReportStatusDTO reportStatusDTO = new ReportStatusDTO();
      reportStatusDTO.setPeriodName(lastRequest.getPeriod().getName());
      reportStatusDTO.setPeriodId(lastRequest.getPeriod().getId());
      reportStatusDTO.setStatus(lastRequest.getStatus().toString());
      reportStatusDTO.setProgramId(programId);
      reportStatusDTO.setFacilityId(facilityId);
      reportStatusDTO.setId(lastRequest.getId());

      results.add(reportStatusDTO);
    }

    for (ProcessingPeriod period : emptyIfNull(periods)) {
      if (lastRequest == null || !lastRequest.getPeriodId().equals(period.getId())) {
        ReportStatusDTO reportStatusDTO = new ReportStatusDTO();

        reportStatusDTO.setPeriodName(period.getName());
        reportStatusDTO.setPeriodId(period.getId());
        reportStatusDTO.setProgramId(programId);
        reportStatusDTO.setFacilityId(facilityId);

        results.add(reportStatusDTO);
      }
    }
    return results;
  }

  public VaccineReport getById(Long id) {
    VaccineReport report = repository.getByIdWithFullDetails(id);
    report.setTabVisibilitySettings(tabVisibilityService.getVisibilityForProgram(report.getProgramId()));
    DateTime periodStartDate = new DateTime(report.getPeriod().getStartDate());
    report.setFacilityDemographicEstimates(annualFacilityDemographicEstimateService.getEstimateValuesForFacility(report.getFacilityId(), report.getProgramId(), periodStartDate.getYear()));
    return report;
  }

  public Long getReportIdForFacilityAndPeriod(Long facilityId, Long periodId){
    return repository.getReportIdForFacilityAndPeriod(facilityId, periodId);
  }

  private List<DiseaseLineItem> getDiseaseSurveillance(Long reportId, Long facilityId, Long periodId, Long zoneId) {
    if (facilityId != null && facilityId !=0 ){

      return repository.getDiseaseSurveillance(reportId);
    }

    return repository.getDiseaseSurveillanceAggregateReport(periodId, zoneId);
  }

  private List<ColdChainLineItem> getColdChain(Long reportId, Long facilityId, Long periodId, Long zoneId) {
    if (facilityId != null && facilityId !=0 ) {
      return repository.getColdChain(reportId);
    }
    return repository.getColdChainAggregateReport(periodId, zoneId);
  }

  private List<AdverseEffectLineItem> getAdverseEffectReport(Long reportId, Long facilityId, Long periodId, Long zoneId) {
    if (facilityId != null && facilityId != 0) {
      return repository.getAdverseEffectReport(reportId);
    }
    return repository.getAdverseEffectAggregateReport(periodId, zoneId);
  }

  private List<HashMap<String, Object>> getVaccineCoverageReport(Long reportId, Long facilityId, Long periodId, Long zoneId) {
    if (facilityId != null && facilityId !=0 ){

      return repository.getVaccineCoverageReport(reportId);
    }
    return repository.getVaccineCoverageAggregateReport(periodId, zoneId);
  }

  private List<VaccineReport> getImmunizationSession(Long reportId, Long facilityId, Long periodId, Long zoneId) {
    if (facilityId != null && facilityId != 0){
      return repository.getImmunizationSession(reportId);
    }
    return repository.getImmunizationSessionAggregate(periodId, zoneId);
  }

  private List<HashMap<String, Object>> getVaccineReport(Long reportId, Long facilityId, Long periodId, Long zoneId) {
    if (facilityId != null && facilityId != 0){
      return repository.getVaccinationReport(VACCINE_REPORT_VACCINE_CATEGORY_CODE, reportId);
    }else{

      return repository.getVaccinationAggregateByGeoZoneReport(VACCINE_REPORT_VACCINE_CATEGORY_CODE, periodId, zoneId);
    }
  }

  private List<HashMap<String, Object>> getSyringeAndSafetyBoxReport(Long reportId, Long facilityId, Long periodId, Long zoneId) {
    if (facilityId != null && facilityId != 0) {
      return repository.getVaccinationReport(VACCINE_REPORT_SYRINGES_CATEGORY_CODE, reportId);
    }
    return repository.getVaccinationAggregateByGeoZoneReport(VACCINE_REPORT_SYRINGES_CATEGORY_CODE, periodId, zoneId);

  }

  private List<HashMap<String, Object>> getVitaminsReport(Long reportId, Long facilityId, Long periodId, Long zoneId) {
    if (facilityId != null && facilityId != 0) {
      return repository.getVaccinationReport(VACCINE_REPORT_VITAMINS_CATEGORY_CODE, reportId);
    }
    return repository.getVaccinationAggregateByGeoZoneReport(VACCINE_REPORT_VITAMINS_CATEGORY_CODE, periodId, zoneId);
  }

  private List<HashMap<String, Object>> getTargetPopulation(Long facilityId, Long periodId, Long zoneId) {
    if (facilityId != null && facilityId != 0) {
      return repository.getTargetPopulation(facilityId, periodId);
    }
    return repository.getTargetPopulationAggregateByGeoZone(periodId, zoneId);
  }

  private List<VitaminSupplementationLineItem> getVitaminSupplementationReport(Long reportId, Long facilityId, Long periodId, Long zoneId) {
    if (facilityId != null && facilityId != 0) {

      return repository.getVitaminSupplementationReport(reportId);
    }
    return repository.getVitaminSupplementationAggregateReport(periodId, zoneId);
  }
  private List<HashMap<String, Object>> getDropOuts(Long reportId, Long facilityId, Long periodId, Long zoneId) {
    if (facilityId != null && facilityId != 0) {
      return repository.getDropOuts(reportId);
    }
    return repository.getAggregateDropOuts(periodId, zoneId);
  }

  public List<HashMap<String, Object>> vaccineUsageTrend(String facilityCode, String productCode, Long periodId, Long zoneId){

    if (zoneId == -1) {
      zoneId = getNationalZoneId();
    }

    if ((facilityCode == null || facilityCode.isEmpty()) && periodId != 0) { // Return aggregated data for selected geographic zone

      return repository.vaccineUsageTrendByGeographicZone(periodId, zoneId, productCode);
    } else {
      return repository.vaccineUsageTrend(facilityCode, productCode);
    }
  }

  public Map<String, Object> getMonthlyVaccineReport(Long facilityId, Long periodId, Long zoneId) {

    Map<String, Object> data = new HashMap();
    Long reportId = null;

    if (facilityId != null && facilityId != 0) { // Return aggregated data for the selected geozone
      reportId = getReportIdForFacilityAndPeriod(facilityId, periodId);

    }

    if (zoneId == -1) {
      zoneId = getNationalZoneId();
    }

    data.put("vaccination", getVaccineReport(reportId, facilityId, periodId, zoneId));
    data.put("diseaseSurveillance", getDiseaseSurveillance(reportId, facilityId, periodId, zoneId));
    data.put("vaccineCoverage", getVaccineCoverageReport(reportId, facilityId, periodId, zoneId));
    data.put("immunizationSession", getImmunizationSession(reportId, facilityId, periodId, zoneId));
    data.put("vitaminSupplementation", getVitaminSupplementationReport(reportId, facilityId, periodId, zoneId));
    data.put("adverseEffect", getAdverseEffectReport(reportId, facilityId, periodId, zoneId));
    data.put("coldChain", getColdChain(reportId, facilityId, periodId, zoneId));
    data.put("targetPopulation", getTargetPopulation(facilityId, periodId, zoneId));
    data.put("syringes", getSyringeAndSafetyBoxReport(reportId, facilityId, periodId, zoneId));
    data.put("vitamins", getVitaminsReport(reportId, facilityId, periodId, zoneId));
    data.put("dropOuts", getDropOuts(reportId, facilityId, periodId, zoneId));


    return data;
  }


  private Long getNationalZoneId() {
    return repository.getNationalZone().getId();
  }

}
