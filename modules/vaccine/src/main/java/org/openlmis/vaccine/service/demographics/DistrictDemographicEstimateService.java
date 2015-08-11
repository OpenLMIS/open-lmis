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
