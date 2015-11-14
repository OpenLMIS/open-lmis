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
import org.openlmis.demographics.domain.AnnualDistrictEstimateEntry;
import org.openlmis.demographics.domain.EstimateCategory;
import org.openlmis.demographics.dto.EstimateForm;
import org.openlmis.demographics.dto.EstimateFormLineItem;
import org.openlmis.demographics.helpers.ListUtil;
import org.openlmis.demographics.repository.AnnualDistrictEstimateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AnnualDistrictDemographicEstimateService {

  @Autowired
  EstimateCategoryService estimateCategoryService;

  @Autowired
  private AnnualDistrictEstimateRepository repository;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private CommaSeparator commaSeparator;


  public void save(EstimateForm estimateForm, Long userId) {
    for (EstimateFormLineItem dto : ListUtil.emptyIfNull(estimateForm.getEstimateLineItems())) {
      for (AnnualDistrictEstimateEntry estimate : ListUtil.emptyIfNull(dto.getDistrictEstimates())) {
        AnnualDistrictEstimateEntry existingEstimateEntry = repository.getEntryBy(estimate.getYear(), estimate.getDistrictId(), estimate.getProgramId(), estimate.getDemographicEstimateId());
        if (existingEstimateEntry != null && (!existingEstimateEntry.getId().equals(estimate.getId()) || existingEstimateEntry.getIsFinal())) {
          continue;
        }
        estimate.setDistrictId(dto.getId());
        if (estimate.getId() == null) {
          estimate.setCreatedBy(userId);
          repository.insert(estimate);
        } else {
          estimate.setModifiedBy(userId);
          repository.update(estimate);
        }
      }
    }
  }

  public void finalizeEstimate(EstimateForm form, Long userId) {
    this.save(form, userId);
    for (EstimateFormLineItem dto : ListUtil.emptyIfNull(form.getEstimateLineItems())) {
      for (AnnualDistrictEstimateEntry est : ListUtil.emptyIfNull(dto.getDistrictEstimates())) {
        est.setDistrictId(dto.getId());
        if (est.getId() != null) {
          est.setModifiedBy(userId);
          est.setModifiedDate(new Date());
          est.setIsFinal(true);
          repository.finalizeEstimate(est);
        }
      }
    }
  }

  public void undoFinalize(EstimateForm form, Long userId) {
    for (EstimateFormLineItem dto : ListUtil.emptyIfNull(form.getEstimateLineItems())) {
      for (AnnualDistrictEstimateEntry est : ListUtil.emptyIfNull(dto.getDistrictEstimates())) {
        est.setDistrictId(dto.getId());
        if (est.getId() != null) {
          est.setModifiedBy(userId);
          est.setModifiedDate(new Date());
          est.setIsFinal(false);
          repository.undoFinalize(est);
        }
      }
    }
  }

  private static List<AnnualDistrictEstimateEntry> getEmptyEstimateObjects(List<EstimateCategory> categories, Long districtId, Long programId, Integer year) {
    List<AnnualDistrictEstimateEntry> result = new ArrayList<>();
    for (EstimateCategory category : categories) {
      AnnualDistrictEstimateEntry estimate = new AnnualDistrictEstimateEntry();
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

  public EstimateForm getEstimateForm(Integer year, Long programId, Long userId) {
    EstimateForm form = new EstimateForm();
    List<EstimateCategory> categories = estimateCategoryService.getAll();
    form.setEstimateLineItems(new ArrayList<EstimateFormLineItem>());

    List<Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, RightName.MANAGE_DEMOGRAPHIC_ESTIMATES);
    String facilityIds = commaSeparator.commaSeparateIds(facilities);

    List<EstimateFormLineItem> districts = repository.getDistrictLineItems(facilityIds);

    for (EstimateFormLineItem dto : districts) {
      dto.setDistrictEstimates(repository.getDistrictEstimates(year, dto.getId(), programId));
      dto.setFacilityEstimates(repository.getFacilityEstimateAggregate(year, dto.getId(), programId));
      if (dto.getDistrictEstimates().isEmpty()) {
        dto.setDistrictEstimates(getEmptyEstimateObjects(categories, dto.getId(), programId, year));
      }
      form.getEstimateLineItems().add(dto);
    }
    return form;
  }
}
