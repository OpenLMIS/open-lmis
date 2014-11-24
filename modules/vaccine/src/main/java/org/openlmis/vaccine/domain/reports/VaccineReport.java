/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.domain.reports;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.vaccine.domain.VaccineDisease;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.openlmis.vaccine.dto.CoverageLineItemDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VaccineReport extends BaseModel {
  private Long   periodId;
  private Long   programId;
  private Long   facilityId;
  private String status;
  private Long   supervisoryNodeId;

  private List<LogisticsLineItem> logisticsLineItems;
  private List<AdverseEffectLineItem> adverseEffectLineItems;

  @JsonIgnore
  private List<VaccineCoverageItem> coverageItems;

  private List<CoverageLineItemDTO> coverageLineItems;
  private List<DiseaseLineItem> diseaseLineItems;


  public void initializeLogisticsLineItems(List<ProgramProduct> programProducts){
    logisticsLineItems = new ArrayList<>();
    for(ProgramProduct pp: programProducts){
      LogisticsLineItem item = new LogisticsLineItem();

      item.setReportId(id);

      item.setProductId(pp.getProduct().getId());
      item.setProductCode(pp.getProduct().getCode());
      item.setProductName(pp.getProduct().getName());

      item.setDisplayOrder(pp.getDisplayOrder());

      logisticsLineItems.add(item);
    }

  }

  public void initializeDiseaseLineItems(List<VaccineDisease> diseases) {
    diseaseLineItems = new ArrayList<>();
    for(VaccineDisease disease: diseases){
      DiseaseLineItem lineItem = new DiseaseLineItem();
      lineItem.setReportId(id);
      lineItem.setDiseaseId(disease.getId());
      lineItem.setDiseaseName(disease.getName());
      lineItem.setDisplayOrder(disease.getDisplayOrder());

      diseaseLineItems.add(lineItem);
   }
  }

  public void initializeCoverageLineItems(List<VaccineProductDose> dosesToCover) {
    coverageItems = new ArrayList<>();
    for(VaccineProductDose dose: dosesToCover){
      VaccineCoverageItem item = new VaccineCoverageItem();

      item.setReportId(id);
      item.setDoseId(dose.getDoseId());
      item.setIsActive(dose.getIsActive());
      item.setProductId(dose.getProductId());

      coverageItems.add(item);
    }
  }
}
