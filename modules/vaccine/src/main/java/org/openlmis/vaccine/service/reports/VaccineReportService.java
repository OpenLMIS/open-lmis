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

import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.vaccine.domain.VaccineDisease;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.openlmis.vaccine.repository.reports.VaccineReportRepository;
import org.openlmis.vaccine.service.DiseaseService;
import org.openlmis.vaccine.service.VaccineProductDoseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
  VaccineProductDoseService productDoseService;

  public VaccineReport InitiateReport(Long facilityId, Long programId, Long periodId){
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

    repository.insert(report);

    List<ProgramProduct> programProducts = programProductService.getActiveByProgram(programId);
    List<VaccineDisease> diseases = diseaseService.getAll();
    List<VaccineProductDose> dosesToCover = productDoseService.getForProgram(programId);

    // 1. copy the products list and initiate the logistics tab.
    report.initializeLogisticsLineItems(programProducts);

    // 2. copy the product + dosage settings and initiate the coverage tab.
    report.initializeCoverageLineItems(dosesToCover);

    // 3. copy the disease list and initiate the disease tab.
    report.initializeDiseaseLineItems(diseases);

    // 4. initiate the cold chain tab.
    //TODO:

    // save all the child records
    lLineItemService.saveLogisticsLineItems(report.getLogisticsLineItems());
    lLineItemService.saveDiseaseLineItems(report.getDiseaseLineItems());
    lLineItemService.saveCoverageLineItems(report.getCoverageItems());
    return report;
  }
}
