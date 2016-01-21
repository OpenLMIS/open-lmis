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

import org.apache.commons.collections.CollectionUtils;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RightName;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.openlmis.demographics.domain.EstimateCategory;
import org.openlmis.demographics.dto.EstimateForm;
import org.openlmis.demographics.dto.EstimateFormLineItem;
import org.openlmis.demographics.helpers.ListUtil;
import org.openlmis.demographics.repository.AnnualFacilityEstimateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AnnualFacilityDemographicEstimateService {

  @Autowired
  EstimateCategoryService estimateCategoryService;

  @Autowired
  private AnnualFacilityEstimateRepository repository;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private CommaSeparator commaSeparator;

  @Autowired
  private SupervisoryNodeService supervisoryNodeService;

  @Autowired
  private RequisitionGroupService requisitionGroupService;

  public void save(EstimateForm estimateForm, Long userId) {
    for (EstimateFormLineItem dto : ListUtil.emptyIfNull(estimateForm.getEstimateLineItems())) {
      for (AnnualFacilityEstimateEntry estimate : ListUtil.emptyIfNull(dto.getFacilityEstimates())) {

        AnnualFacilityEstimateEntry existingEstimateEntry = repository.getEntryBy(estimate.getYear(), estimate.getFacilityId(), estimate.getProgramId(), estimate.getDemographicEstimateId());

        if (existingEstimateEntry != null && (!existingEstimateEntry.getId().equals(estimate.getId()) || existingEstimateEntry.getIsFinal())) {
          continue;
        }
        estimate.setFacilityId(dto.getId());

        if (!estimate.hasId()) {
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
      for (AnnualFacilityEstimateEntry est : ListUtil.emptyIfNull(dto.getFacilityEstimates())) {
        est.setFacilityId(dto.getId());
        if (est.hasId()) {
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
      for (AnnualFacilityEstimateEntry est : ListUtil.emptyIfNull(dto.getFacilityEstimates())) {
        est.setFacilityId(dto.getId());
        if (est.hasId()) {
          est.setModifiedBy(userId);
          est.setModifiedDate(new Date());
          est.setIsFinal(false);
          repository.undoFinalize(est);
        }
      }
    }
  }

  private static List<AnnualFacilityEstimateEntry> createDefaultEstimateEntries(List<EstimateCategory> categories, Long facilityId, Long programId, Integer year, Boolean includeDetails) {

    List<AnnualFacilityEstimateEntry> result = new ArrayList<>();

    for (EstimateCategory category : categories) {
      AnnualFacilityEstimateEntry estimate = new AnnualFacilityEstimateEntry();
      estimate.setYear(year);
      estimate.setFacilityId(facilityId);
      estimate.setIsFinal(false);
      estimate.setProgramId(programId);
      estimate.setConversionFactor(category.getDefaultConversionFactor());
      estimate.setDemographicEstimateId(category.getId());
      estimate.setValue(0L);
      if (includeDetails) {
        estimate.setCategory(category);
      }
      result.add(estimate);
    }

    return result;
  }

  public EstimateForm getEstimateForm(Long userId, Long programId, Integer year) {
    EstimateForm form = new EstimateForm();
    List<EstimateCategory> categories = estimateCategoryService.getAll();
    form.setEstimateLineItems(new ArrayList<EstimateFormLineItem>());
    List<SupervisoryNode> supervisoryNodes = supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(userId, programId, RightName.MANAGE_DEMOGRAPHIC_ESTIMATES);
    List<RequisitionGroup> requisitionGroups = requisitionGroupService.getRequisitionGroupsBy(supervisoryNodes);

    List<EstimateFormLineItem> facilities = repository.getFacilityList(programId, commaSeparator.commaSeparateIds(requisitionGroups));
    for (EstimateFormLineItem facility : facilities) {
      facility.setFacilityEstimates(repository.getFacilityEstimate(year, facility.getId(), programId));
      if (facility.getFacilityEstimates().isEmpty()) {
        facility.setFacilityEstimates(createDefaultEstimateEntries(categories, facility.getId(), programId, year, false));
      }
      form.getEstimateLineItems().add(facility);
    }
    return form;
  }

  public List<AnnualFacilityEstimateEntry> getEstimateValuesForFacility(Long facilityId, Long programId, Integer year) {
    List<AnnualFacilityEstimateEntry> estimates = repository.getFacilityEstimateWithDetails(year, facilityId, programId);
    if (CollectionUtils.isEmpty(estimates)) {
      Facility facility = facilityService.getById(facilityId);
      List<EstimateCategory> categories = estimateCategoryService.getAll();
      estimates = createDefaultEstimateEntries(categories, facility.getId(), programId, year, true);
      for (AnnualFacilityEstimateEntry estimate : estimates) {
        estimate.calculateAndSetValue(facility.getCatchmentPopulation());
      }
    }
    return estimates;
  }


}
