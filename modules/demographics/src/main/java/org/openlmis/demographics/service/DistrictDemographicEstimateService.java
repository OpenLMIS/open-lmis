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

package org.openlmis.demographics.service;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RightName;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.demographics.domain.DemographicEstimateCategory;
import org.openlmis.demographics.domain.DistrictDemographicEstimate;
import org.openlmis.demographics.dto.DemographicEstimateForm;
import org.openlmis.demographics.dto.DemographicEstimateLineItem;
import org.openlmis.demographics.helpers.ListUtil;
import org.openlmis.demographics.repository.DistrictDemographicEstimateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DistrictDemographicEstimateService {

  @Autowired
  DemographicEstimateCategoryService estimateCategoryService;

  @Autowired
  private DistrictDemographicEstimateRepository repository;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private CommaSeparator commaSeparator;

  @Autowired
  private SupervisoryNodeService supervisoryNodeService;

  @Autowired
  private RequisitionGroupService requisitionGroupService;

  public void save(DemographicEstimateForm estimate, Long userId){
    for(DemographicEstimateLineItem dto: ListUtil.emptyIfNull(estimate.getEstimateLineItems())){
      for(DistrictDemographicEstimate est: ListUtil.emptyIfNull(dto.getDistrictEstimates())){
        est.setDistrictId(dto.getId());
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

  public void finalize(DemographicEstimateForm form, Long userId) {
    this.save(form, userId);
    for(DemographicEstimateLineItem dto: ListUtil.emptyIfNull(form.getEstimateLineItems())) {
      for (DistrictDemographicEstimate est : ListUtil.emptyIfNull(dto.getDistrictEstimates())) {
        est.setDistrictId(dto.getId());
        if(est.getId() != null){
          est.setModifiedBy(userId);
          est.setModifiedDate(new Date());
          est.setIsFinal(true);
          repository.finalize(est);
        }
      }
    }
  }

  public void undoFinalize(DemographicEstimateForm form, Long userId) {
    for(DemographicEstimateLineItem dto: ListUtil.emptyIfNull(form.getEstimateLineItems())) {
      for (DistrictDemographicEstimate est : ListUtil.emptyIfNull(dto.getDistrictEstimates())) {
        est.setDistrictId(dto.getId());
        if(est.getId() != null){
          est.setModifiedBy(userId);
          est.setModifiedDate(new Date());
          est.setIsFinal(false);
          repository.undoFinalize(est);
        }
      }
    }
  }

  private List<DistrictDemographicEstimate> getEmptyEstimateObjects(List<DemographicEstimateCategory> categories, Long districtId, Long programId, Integer year) {
    List<DistrictDemographicEstimate> result = new ArrayList<>();
    for(DemographicEstimateCategory category: categories){
      DistrictDemographicEstimate estimate = new DistrictDemographicEstimate();
      estimate.setYear(year);
      estimate.setDistrictId(districtId);
      estimate.setIsFinal(false);
      estimate.setProgramId(programId);
      estimate.setConversionFactor(category.getDefaultConversionFactor());
      estimate.setDemographicEstimateId(category.getId());
      estimate.setValue(0L);
      result.add(estimate);
    }
    return result;
  }

  public DemographicEstimateForm getEstimateForm(Integer year, Long programId, Long userId) {
    DemographicEstimateForm form = new DemographicEstimateForm();
    List<DemographicEstimateCategory> categories = estimateCategoryService.getAll();
    form.setEstimateLineItems(new ArrayList<DemographicEstimateLineItem>());

    List<Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, RightName.MANAGE_DEMOGRAPHIC_ESTIMATES );
    String facilityIds = commaSeparator.commaSeparateIds(facilities);

    List<DemographicEstimateLineItem> districts =  repository.getDistrictLineItems(facilityIds);

    for(DemographicEstimateLineItem dto : districts){
      dto.setDistrictEstimates(repository.getDistrictEstimate(year, dto.getId(), programId));
      dto.setFacilityEstimates(repository.getFacilityEstimateAggregate(year, dto.getId(), programId));
      if( dto.getDistrictEstimates().size() == 0 ){
        dto.setDistrictEstimates(getEmptyEstimateObjects(categories, dto.getId(), programId, year));
      }
      form.getEstimateLineItems().add(dto);
    }
    return form;
  }
}
