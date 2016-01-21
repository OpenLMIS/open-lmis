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

package org.openlmis.demographics.repository;

import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.openlmis.demographics.dto.EstimateFormLineItem;
import org.openlmis.demographics.repository.mapper.AnnualFacilityEstimateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AnnualFacilityEstimateRepository {

  @Autowired
  AnnualFacilityEstimateMapper mapper;

  public List<AnnualFacilityEstimateEntry> getFacilityEstimate(Integer year, Long facilityId, Long programId) {
    return mapper.getEstimatesForFacility(year, facilityId, programId);
  }

  public List<AnnualFacilityEstimateEntry> getFacilityEstimateWithDetails(Integer year, Long facilityId, Long programId) {
    return mapper.getEstimatesForFacilityWithDetails(year, facilityId, programId);
  }

  public Integer insert(AnnualFacilityEstimateEntry estimate){
    return mapper.insert(estimate);
  }

  public Integer update(AnnualFacilityEstimateEntry estimate){
    return mapper.update(estimate);
  }

  public AnnualFacilityEstimateEntry getEntryBy(Integer year, Long facilityId, Long programId, Long categoryId){
    return mapper.getEntryBy(year, facilityId, programId, categoryId);
  }

  public List<EstimateFormLineItem> getFacilityList(Long programId, String requsitionGroupIds) {
    return mapper.getFacilityList(programId, requsitionGroupIds);
  }

  public Integer finalizeEstimate(AnnualFacilityEstimateEntry estimate){
    return mapper.finalizeEstimate(estimate);
  }

  public Integer undoFinalize(AnnualFacilityEstimateEntry estimate){
    return mapper.undoFinalize(estimate);
  }
}
