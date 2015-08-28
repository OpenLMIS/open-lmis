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

package org.openlmis.vaccine.repository.demographics;

import org.openlmis.vaccine.domain.demographics.FacilityDemographicEstimate;
import org.openlmis.vaccine.repository.mapper.demographics.FacilityDemographicEstimateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FacilityDemographicEstimateRepository {

  @Autowired
  FacilityDemographicEstimateMapper mapper;

  public List<FacilityDemographicEstimate> getFacilityEstimate(Integer year, Long facilityId){
    return mapper.getEstimatesForFacility(year,facilityId);
  }

  public Integer insert(FacilityDemographicEstimate estimate){
    return mapper.insert(estimate);
  }

  public Integer update(FacilityDemographicEstimate estimate){
    return mapper.update(estimate);
  }
}
