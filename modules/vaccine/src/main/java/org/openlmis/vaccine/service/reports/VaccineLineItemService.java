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

import org.openlmis.vaccine.domain.reports.*;
import org.openlmis.vaccine.repository.reports.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VaccineLineItemService {

  @Autowired
  VaccineReportLogisticsLineItemRepository repository;

  @Autowired
  VaccineReportDiseaseLineItemRepository diseaseLineItemRepository;

  @Autowired
  VaccineReportCoverageItemRepository coverageItemRepository;

  @Autowired
  VaccineReportAdverseEffectRepository adverseLineItemRepository;

  @Autowired
  VaccineReportCampaignLineItemRepository campaignLineItemRepository;

  @Autowired
  VaccineReportColdChainRepository coldChainRepository;

  @Autowired
  VitaminSupplementationLineItemRepository vitaminSupplementationLineItemRepository;

  public void saveLogisticsLineItems(List<LogisticsLineItem> lineItems){
    for(LogisticsLineItem lineItem: lineItems){
      if(lineItem.getId() == null){
        repository.insert(lineItem);
      }else{
        repository.update(lineItem);
      }
    }
  }

  public void saveDiseaseLineItems(List<DiseaseLineItem> lineItems){
    for(DiseaseLineItem lineItem: lineItems){
      if(lineItem.getId() == null){
        diseaseLineItemRepository.insert(lineItem);
      }else{
        diseaseLineItemRepository.update(lineItem);
      }
    }
  }

  public void saveCoverageLineItems(List<VaccineCoverageItem> lineItems) {
    for(VaccineCoverageItem lineItem: lineItems){
      if(lineItem.getId() == null){
        coverageItemRepository.insert(lineItem);
      }else{
        coverageItemRepository.update(lineItem);
      }
    }
  }

  public void saveAdverseEffectLineItems(List<AdverseEffectLineItem> adverseEffectLineItems, Long reportId) {
    for(AdverseEffectLineItem lineItem: adverseEffectLineItems){
      lineItem.setReportId(reportId);
      if(lineItem.getId() == null){
        adverseLineItemRepository.insert(lineItem);
      }
      else{
        adverseLineItemRepository.update(lineItem);
      }
    }
  }

  public void saveCampaignLineItems(List<CampaignLineItem> campaignLineItems, Long reportId) {
    for(CampaignLineItem lineItem: campaignLineItems){
      lineItem.setReportId(reportId);
      if(lineItem.getId() == null){
        campaignLineItemRepository.insert(lineItem);
      }
      else{
        campaignLineItemRepository.update(lineItem);
      }
    }
  }

  public void saveColdChainLIneItems(List<ColdChainLineItem> coldChainLineItems, Long reportId) {
    for(ColdChainLineItem lineItem: coldChainLineItems){
      lineItem.setReportId(reportId);
      if(lineItem.getId() == null){
        coldChainRepository.insert(lineItem);
      }
      else{
        coldChainRepository.update(lineItem);
      }
    }
  }

  public void saveVitaminLineItems(List<VitaminSupplementationLineItem> vitaminSupplementationLineItems, Long reportId) {
    for(VitaminSupplementationLineItem lineItem: vitaminSupplementationLineItems){
      lineItem.setReportId(reportId);
      if(lineItem.getId() == null){
        vitaminSupplementationLineItemRepository.insert(lineItem);
      }
      else{
        vitaminSupplementationLineItemRepository.update(lineItem);
      }
    }
  }
}
