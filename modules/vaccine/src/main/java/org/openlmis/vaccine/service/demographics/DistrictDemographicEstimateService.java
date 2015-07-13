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

import org.openlmis.core.domain.GeographicZone;
import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.openlmis.vaccine.domain.demographics.DistrictDemographicEstimate;
import org.openlmis.vaccine.dto.DemographicEstimateForm;
import org.openlmis.vaccine.dto.DemographicEstimateLineItem;
import org.openlmis.vaccine.repository.demographics.DistrictDemographicEstimateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.openlmis.vaccine.utils.ListUtil.emptyIfNull;

@Service
public class DistrictDemographicEstimateService {

  @Autowired
  DemographicEstimateCategoryService estimateCategoryService;

  @Autowired
  private DistrictDemographicEstimateRepository repository;

  public void save(DemographicEstimateForm estimate, Long userId){
    for(DemographicEstimateLineItem dto: emptyIfNull(estimate.getEstimateLineItems())){
      for(DistrictDemographicEstimate est: emptyIfNull(dto.getDistrictEstimates())){
        est.setDistrictId(dto.getDistrictId());
        if(est.getId() == null){
          est.setCreatedBy(userId);
          repository.insert(est);
        }else{
          est.setModifiedBy(userId);
          repository.update(est);
        }
      }
    }
  }

  private List<DistrictDemographicEstimate> getEmptyEstimateObjects(List<DemographicEstimateCategory> categories, Long districtId , Integer year){
    List<DistrictDemographicEstimate> result = new ArrayList<>();
    for(DemographicEstimateCategory category: categories){
      DistrictDemographicEstimate estimate = new DistrictDemographicEstimate();
      estimate.setYear(year);
      estimate.setDistrictId(districtId);
      estimate.setConversionFactor(category.getDefaultConversionFactor());
      estimate.setDemographicEstimateId(category.getId());
      estimate.setValue(0L);
      result.add(estimate);
    }
    return result;
  }

  public DemographicEstimateForm getEstimateFor(Integer year){
    DemographicEstimateForm form = new DemographicEstimateForm();
    List<DemographicEstimateCategory> categories = estimateCategoryService.getAll();
    form.setEstimateLineItems(new ArrayList<DemographicEstimateLineItem>());

    List<GeographicZone> districts =  repository.getDistricts();
    // Not scalable - please refactor this.

    for(GeographicZone district : districts){
      DemographicEstimateLineItem dto = new DemographicEstimateLineItem();
      dto.setDistrictId(district.getId());
      dto.setCode(district.getCode());
      dto.setName(district.getName());
      dto.setDistrictEstimates(repository.getDistrictEstimate(year, district.getId()));
      if( dto.getDistrictEstimates().size() == 0 ){
        dto.setDistrictEstimates(getEmptyEstimateObjects(categories, district.getId(), year));
      }
      form.getEstimateLineItems().add(dto);
    }

    return form;
  }
}
