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

import org.openlmis.vaccine.domain.reports.*;
import org.openlmis.vaccine.repository.reports.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.openlmis.vaccine.utils.ListUtil.emptyIfNull;

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

  public void saveLogisticsLineItems(List<LogisticsLineItem> lineItems, Long reportId){
    for(LogisticsLineItem lineItem: emptyIfNull(lineItems)){
      if(!lineItem.hasId()){
        lineItem.setReportId(reportId);
        repository.insert(lineItem);
      }else{
        repository.update(lineItem);
      }
    }
  }

  public void saveDiseaseLineItems(List<DiseaseLineItem> lineItems, Long reportId){
    for(DiseaseLineItem lineItem: emptyIfNull(lineItems)){
      if(!lineItem.hasId()){
        lineItem.setReportId(reportId);
        diseaseLineItemRepository.insert(lineItem);
      }else{
        diseaseLineItemRepository.update(lineItem);
      }
    }
  }

  public void saveCoverageLineItems(List<VaccineCoverageItem> lineItems, Long reportId) {
    for(VaccineCoverageItem lineItem: emptyIfNull(lineItems)){
      if(!lineItem.hasId()){
        lineItem.setReportId(reportId);
        coverageItemRepository.insert(lineItem);
      }else{
        coverageItemRepository.update(lineItem);
      }
    }
  }

  public void saveAdverseEffectLineItems(List<AdverseEffectLineItem> adverseEffectLineItems, Long reportId) {
    for(AdverseEffectLineItem lineItem: emptyIfNull(adverseEffectLineItems)){
      lineItem.setReportId(reportId);
      if(!lineItem.hasId()){
        adverseLineItemRepository.insert(lineItem);
      }
      else{
        adverseLineItemRepository.update(lineItem);
      }
    }
  }

  public void saveCampaignLineItems(List<CampaignLineItem> campaignLineItems, Long reportId) {
    for(CampaignLineItem lineItem: emptyIfNull(campaignLineItems)){
      lineItem.setReportId(reportId);
      if(!lineItem.hasId()){
        campaignLineItemRepository.insert(lineItem);
      }
      else{
        campaignLineItemRepository.update(lineItem);
      }
    }
  }

  public void saveColdChainLIneItems(List<ColdChainLineItem> coldChainLineItems, Long reportId) {
    for(ColdChainLineItem lineItem: emptyIfNull(coldChainLineItems)){
      lineItem.setReportId(reportId);
      if(!lineItem.hasId()){
        coldChainRepository.insert(lineItem);
      }
      else{
        coldChainRepository.update(lineItem);
      }
    }
  }

  public void saveVitaminLineItems(List<VitaminSupplementationLineItem> vitaminSupplementationLineItems, Long reportId) {
    for(VitaminSupplementationLineItem lineItem: emptyIfNull(vitaminSupplementationLineItems)){
      lineItem.setReportId(reportId);
      if(!lineItem.hasId()){
        vitaminSupplementationLineItemRepository.insert(lineItem);
      }
      else{
        vitaminSupplementationLineItemRepository.update(lineItem);
      }
    }
  }
}
