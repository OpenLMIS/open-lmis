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

  public DemographicEstimateForm getEstimateFor(Long userId, Integer year){
    DemographicEstimateForm form = new DemographicEstimateForm();
    List<DemographicEstimateCategory> categories = estimateCategoryService.getAll();
    form.setEstimateLineItems(new ArrayList<DemographicEstimateLineItem>());
    List<Facility> facilities =  facilityService.getForUserAndRights(userId, RightName.MANAGE_DEMOGRAPHIC_ESTIMATES);

    for(Facility facility : facilities){
      DemographicEstimateLineItem dto = new DemographicEstimateLineItem();
      dto.setFacilityId(facility.getId());
      dto.setCode(facility.getCode());
      dto.setName(facility.getName());
      dto.setParentName(facility.getGeographicZone().getName());
      dto.setFacilityEstimates(repository.getFacilityEstimate(year, facility.getId()));

      if( dto.getFacilityEstimates().size() == 0 ){
        dto.setFacilityEstimates(getEmptyEstimateObjects(categories, facility.getId(), year));
      }

      form.getEstimateLineItems().add(dto);
    }

    return form;
  }

  public List<FacilityDemographicEstimate> getEstimateValuesForFacility(Long facilityId, Integer year){
    List<FacilityDemographicEstimate> result =  repository.getFacilityEstimate(year, facilityId);
    if(result == null || result.size() == 0){
      Facility facility = facilityService.getById(facilityId);
       ;
      List<DemographicEstimateCategory> categories = estimateCategoryService.getAll();
      result = getEmptyEstimateObjects(categories, facility.getId(), year);
      for(FacilityDemographicEstimate estimate: result){
        estimate.calculateAndSetValue(facility.getCatchmentPopulation());
      }
    }
    return result;
  }
}
