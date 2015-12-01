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

package org.openlmis.vaccine.domain.reports;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.serializer.DateDeserializer;
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.openlmis.vaccine.domain.VaccineDisease;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.openlmis.vaccine.domain.Vitamin;
import org.openlmis.vaccine.domain.VitaminSupplementationAgeGroup;
import org.openlmis.vaccine.domain.config.VaccineIvdTabVisibility;

import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccineReport extends BaseModel {

  private Long periodId;
  private Long programId;
  private Long facilityId;
  private ReportStatus status;
  private Long supervisoryNodeId;
  private ProcessingPeriod period;
  private Facility facility;
  private String majorImmunizationActivities;

  private Long fixedImmunizationSessions;
  private Long outreachImmunizationSessions;
  private Long outreachImmunizationSessionsCanceled;

  @JsonDeserialize(using = DateDeserializer.class)
  private Date submissionDate;

  private List<VaccineIvdTabVisibility> tabVisibilitySettings;

  private List<LogisticsLineItem> logisticsLineItems;
  private List<AdverseEffectLineItem> adverseEffectLineItems;
  private List<CampaignLineItem> campaignLineItems;

  private List<AnnualFacilityEstimateEntry> facilityDemographicEstimates;

  private List<VitaminSupplementationLineItem> vitaminSupplementationLineItems;

  private List<LogisticsColumn> columnTemplate;

  private List<VaccineCoverageItem> coverageLineItems;
  private List<DiseaseLineItem> diseaseLineItems;
  private List<ColdChainLineItem> coldChainLineItems;
  private List<ReportStatusChange> reportStatusChanges;


  public void initializeLogisticsLineItems(List<ProgramProduct> programProducts, VaccineReport previousReport) {
    logisticsLineItems = new ArrayList<>();
    Map<String, LogisticsLineItem> previousLineItemMap = new HashMap<>();
    if(previousReport != null){
      for(LogisticsLineItem lineItem : previousReport.getLogisticsLineItems()){
        previousLineItemMap.put(lineItem.getProductCode(), lineItem);
      }
    }
    for (ProgramProduct pp : programProducts) {
      LogisticsLineItem item = new LogisticsLineItem();

      item.setReportId(id);

      item.setProductId(pp.getProduct().getId());
      item.setProductCode(pp.getProduct().getCode());
      item.setProductName(pp.getProduct().getPrimaryName());
      item.setProductCategory(pp.getProductCategory().getName());
      item.setDisplayOrder(pp.getDisplayOrder());

      if(previousReport != null){
        LogisticsLineItem lineitem = previousLineItemMap.get(item.getProductCode());
        if(lineitem != null){
          item.setOpeningBalance(lineitem.getClosingBalance());
        }
      }
      logisticsLineItems.add(item);
    }

  }

  public void initializeDiseaseLineItems(List<VaccineDisease> diseases) {
    diseaseLineItems = new ArrayList<>();
    for (VaccineDisease disease : diseases) {
      DiseaseLineItem lineItem = new DiseaseLineItem();
      lineItem.setReportId(id);
      lineItem.setDiseaseId(disease.getId());
      lineItem.setDiseaseName(disease.getName());
      lineItem.setDisplayOrder(disease.getDisplayOrder());

      diseaseLineItems.add(lineItem);
    }
  }

  public void initializeCoverageLineItems(List<VaccineProductDose> dosesToCover) {
    coverageLineItems = new ArrayList<>();
    for (VaccineProductDose dose : dosesToCover) {
      VaccineCoverageItem item = new VaccineCoverageItem();
      item.setReportId(id);
      item.setDoseId(dose.getDoseId());
      item.setTrackMale(dose.getTrackMale());
      item.setTrackFemale(dose.getTrackFemale());
      item.setDisplayOrder(dose.getDisplayOrder());
      item.setDisplayName(dose.getDisplayName());
      item.setProductId(dose.getProductId());
      coverageLineItems.add(item);
    }
  }

  public void initializeColdChainLineItems(List<ColdChainLineItem> lineItems) {
    coldChainLineItems = lineItems;
  }

  public void initializeVitaminLineItems(List<Vitamin> vitamins, List<VitaminSupplementationAgeGroup> ageGroups) {
    this.vitaminSupplementationLineItems = new ArrayList<>();
    Long displayOrder = 1L;
    for (Vitamin vitamin : vitamins) {
      for (VitaminSupplementationAgeGroup ageGroup : ageGroups) {
        VitaminSupplementationLineItem item = new VitaminSupplementationLineItem();
        item.setVitaminAgeGroupId(ageGroup.getId());
        item.setDisplayOrder(displayOrder);
        item.setVitaminName(vitamin.getName());
        item.setVaccineVitaminId(vitamin.getId());
        this.vitaminSupplementationLineItems.add(item);
        displayOrder++;
      }
    }
  }

}
