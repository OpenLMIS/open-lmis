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

import org.openlmis.demographics.domain.AnnualDistrictEstimateEntry;
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.openlmis.demographics.dto.EstimateFormLineItem;
import org.openlmis.demographics.repository.mapper.AnnualDistrictEstimateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AnnualDistrictEstimateRepository {

  @Autowired
  AnnualDistrictEstimateMapper mapper;

  public List<AnnualDistrictEstimateEntry> getDistrictEstimates(Integer year, Long districtId, Long programId) {
    return mapper.getEstimatesForDistrict(year, districtId, programId);
  }

  public Integer insert(AnnualDistrictEstimateEntry estimate){
    return mapper.insert(estimate);
  }

  public Integer update(AnnualDistrictEstimateEntry estimate){
    return mapper.update(estimate);
  }

  public List<EstimateFormLineItem> getDistrictLineItems(String facilityIds){
    return mapper.getDistrictLineItems(facilityIds);
  }

  public AnnualDistrictEstimateEntry getEntryBy(Integer year, Long districtId, Long programId, Long categoryId){
    return mapper.getEntryBy(year, districtId, programId, categoryId);
  }

  public List<AnnualFacilityEstimateEntry> getFacilityEstimateAggregate(Integer year, Long districtId, Long programId) {
    return mapper.getFacilityEstimateAggregate(year, districtId, programId);
  }

  public void finalizeEstimate(AnnualDistrictEstimateEntry est) {
    mapper.finalizeEstimate(est);
  }

  public void undoFinalize(AnnualDistrictEstimateEntry est) {
    mapper.undoFinalize(est);
  }
}
