/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.service.demographics;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RightName;
import org.openlmis.core.service.FacilityService;
import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.openlmis.vaccine.domain.demographics.FacilityDemographicEstimate;
import org.openlmis.vaccine.dto.DemographicEstimateLineItem;
import org.openlmis.vaccine.dto.DemographicEstimateForm;
import org.openlmis.vaccine.repository.demographics.FacilityDemographicEstimateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.openlmis.vaccine.utils.ListUtil.emptyIfNull;

@Service
public class FacilityDemographicEstimateService {

  @Autowired
  DemographicEstimateCategoryService estimateCategoryService;

  @Autowired
  private FacilityDemographicEstimateRepository repository;

  @Autowired
  private FacilityService facilityService;

  public void save(DemographicEstimateForm estimate){
    for(DemographicEstimateLineItem dto: emptyIfNull(estimate.getEstimateLineItems())){
      for(FacilityDemographicEstimate est: emptyIfNull(dto.getFacilityEstimates())){
        est.setFacilityId(dto.getFacilityId());
        if(est.getId() == null){
          repository.insert(est);
        }else{
          repository.update(est);
        }
      }
    }
  }

  private List<FacilityDemographicEstimate> getEmptyEstimateObjects(List<DemographicEstimateCategory> categories, Long facilityId , Integer year){
    List<FacilityDemographicEstimate> result = new ArrayList<>();
    for(DemographicEstimateCategory category: categories){
      FacilityDemographicEstimate estimate = new FacilityDemographicEstimate();
      estimate.setYear(year);
      estimate.setFacilityId(facilityId);
      estimate.setConversionFactor(category.getDefaultConversionFactor());
      estimate.setDemographicEstimateId(category.getId());
      estimate.setValue(0L);
      result.add(estimate);
    }
    return result;
  }

  public DemographicEstimateForm getEstimateFor(Long userId, Long programId, Integer year){
    DemographicEstimateForm form = new DemographicEstimateForm();
    List<DemographicEstimateCategory> categories = estimateCategoryService.getAll();
    form.setEstimateLineItems(new ArrayList<DemographicEstimateLineItem>());
    List<Facility> facilities =  facilityService.getUserSupervisedFacilities(userId, programId, RightName.MANAGE_DEMOGRAPHIC_ESTIMATES);
    // Not scalable - please refactor this.

    for(Facility facility : facilities){
      DemographicEstimateLineItem dto = new DemographicEstimateLineItem();
      dto.setFacilityId(facility.getId());
      dto.setCode(facility.getCode());
      dto.setName(facility.getName());
      dto.setFacilityEstimates(repository.getFacilityEstimate(year, facility.getId()));

      if( dto.getFacilityEstimates().size() == 0 ){
        dto.setFacilityEstimates(getEmptyEstimateObjects(categories, facility.getId(), year));
      }

      form.getEstimateLineItems().add(dto);
    }

    return form;
  }
}
