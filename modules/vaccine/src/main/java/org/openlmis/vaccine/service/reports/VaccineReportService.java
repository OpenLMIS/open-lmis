/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.service.reports;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.service.*;
import org.openlmis.vaccine.RequestStatus;
import org.openlmis.vaccine.domain.VaccineDisease;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.openlmis.vaccine.domain.Vitamin;
import org.openlmis.vaccine.domain.VitaminSupplementationAgeGroup;
import org.openlmis.vaccine.domain.reports.ColdChainLineItem;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.openlmis.vaccine.dto.ReportStatusDTO;
import org.openlmis.vaccine.repository.VitaminSupplementationAgeGroupRepository;
import org.openlmis.vaccine.repository.VitaminRepository;
import org.openlmis.vaccine.repository.reports.VaccineReportColdChainRepository;
import org.openlmis.vaccine.repository.reports.VaccineReportRepository;
import org.openlmis.vaccine.service.DiseaseService;
import org.openlmis.vaccine.service.VaccineProductDoseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@NoArgsConstructor
public class VaccineReportService {

  @Autowired
  VaccineReportRepository repository;

  @Autowired
  ProgramProductService programProductService;

  @Autowired
  VaccineLineItemService lLineItemService;

  @Autowired
  DiseaseService diseaseService;

  @Autowired
  ProcessingPeriodRepository periodService;

  @Autowired
  VaccineProductDoseService productDoseService;

  @Autowired
  ConfigurationSettingService settingService;

  @Autowired
  VaccineReportColdChainRepository coldChainRepository;

  @Autowired
  VitaminRepository vitaminRepository;

  @Autowired
  VitaminSupplementationAgeGroupRepository ageGroupRepository;

  @Autowired
  ProgramService programService;


  public VaccineReport initialize(Long facilityId, Long programId, Long periodId){
    // check if the report is already initiated,
    VaccineReport report = repository.getByProgramPeriod(facilityId, programId, periodId);
    // if initiated, return it,
    if(report != null){
      return report;
    }

    report = new VaccineReport();

    report.setFacilityId(facilityId);
    report.setProgramId(programId);
    report.setPeriodId(periodId);
    report.setStatus(RequestStatus.DRAFT.toString());
    repository.insert(report);

    List<ProgramProduct> programProducts = programProductService.getActiveByProgram(programId);
    List<VaccineDisease> diseases = diseaseService.getAll();
    List<VaccineProductDose> dosesToCover = productDoseService.getForProgram(programId);
    List<ColdChainLineItem> coldChainLineItems = coldChainRepository.getNewEquipmentLineItems(programId, facilityId);
    List<Vitamin> vitamins = vitaminRepository.getAll();
    List<VitaminSupplementationAgeGroup> ageGroups = ageGroupRepository.getAll();


    // 1. copy the products list and initiate the logistics tab.
    report.initializeLogisticsLineItems(programProducts);

    // 2. copy the product + dosage settings and initiate the coverage tab.
    report.initializeCoverageLineItems(dosesToCover);

    // 3. copy the disease list and initiate the disease tab.
    report.initializeDiseaseLineItems(diseases);

    // 4. initialize the cold chain line items.
    report.initializeColdChainLineItems(coldChainLineItems);

    report.initializeVitaminLineItems(vitamins, ageGroups);


    // save all the child records
    lLineItemService.saveLogisticsLineItems(report.getLogisticsLineItems());
    lLineItemService.saveDiseaseLineItems(report.getDiseaseLineItems());
    lLineItemService.saveCoverageLineItems(report.getCoverageItems());
    lLineItemService.saveColdChainLIneItems(report.getColdChainLineItems(), report.getId());
    lLineItemService.saveVitaminLineItems(report.getVitaminSupplementationLineItems(), report.getId());
    return report;
  }

  public List<ReportStatusDTO> getPeriodsFor(Long facilityId, Long programId) {
    Date startDate = programService.getProgramStartDate(facilityId, programId);

    // find out which schedule this facility is in?
    Long scheduleId = repository.getScheduleFor(facilityId, programId);
    VaccineReport lastRequest = repository.getLastReport(facilityId, programId);

    if(lastRequest != null){
      lastRequest.setPeriod(periodService.getById(lastRequest.getPeriodId()));
      startDate = lastRequest.getPeriod().getStartDate();
    }

    Long lastPeriodId = lastRequest == null ? null : lastRequest.getPeriodId();
    List<ReportStatusDTO> results = new ArrayList<>();
    // find all periods that are after this period, and before today.

    List<ProcessingPeriod> periods = periodService.getAllPeriodsForDateRange(scheduleId, startDate, new Date());
    if(lastRequest != null && lastRequest.getStatus().equals( RequestStatus.DRAFT.toString())){
      ReportStatusDTO reportStatusDTO = new ReportStatusDTO();
      reportStatusDTO.setPeriodName(lastRequest.getPeriod().getName());
      reportStatusDTO.setPeriodId(lastRequest.getPeriod().getId());
      reportStatusDTO.setStatus(lastRequest.getStatus());
      reportStatusDTO.setProgramId(programId);
      reportStatusDTO.setFacilityId(facilityId);
      reportStatusDTO.setId(lastRequest.getId());

      results.add(reportStatusDTO);
    }

    for(ProcessingPeriod period: periods){
      // avoid duplicate period
      if(lastRequest == null || lastRequest.getPeriodId() != period.getId()){
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

  public void save(VaccineReport report) {
    repository.update(report);
    // save the other user inputs too.
    lLineItemService.saveLogisticsLineItems(report.getLogisticsLineItems());
    lLineItemService.saveDiseaseLineItems(report.getDiseaseLineItems());
    report.flattenCoverageLineItems();
    lLineItemService.saveCoverageLineItems(report.getCoverageItems());
    lLineItemService.saveAdverseEffectLineItems(report.getAdverseEffectLineItems(), report.getId());
    lLineItemService.saveCampaignLineItems(report.getCampaignLineItems(),report.getId());
    lLineItemService.saveColdChainLIneItems(report.getColdChainLineItems(), report.getId());
    lLineItemService.saveVitaminLineItems(report.getVitaminSupplementationLineItems(), report.getId());
  }

  public VaccineReport getById(Long id) {
    VaccineReport report =  repository.getByIdWithFullDetails(id);
    report.prepareCoverageDto();
    report.setTrackCampaignCoverage(settingService.getBoolValue("TRACK_VACCINE_CAMPAIGN_COVERAGE"));
    report.setTrackOutreachCoverage(settingService.getBoolValue("TRACK_VACCINE_OUTREACH_COVERAGE"));
    report.setTabVisibilitySettings(settingService.getSearchResults("VACCINE_TAB%"));
    return report;
  }
}
