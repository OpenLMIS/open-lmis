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

package org.openlmis.vaccine.builders.demographics;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.vaccine.domain.demographics.FacilityDemographicEstimate;

public class FacilityDemographicEstimateBuilder {

  public static final Instantiator<FacilityDemographicEstimate> defaultFacilityDemographicEstimate = new Instantiator<FacilityDemographicEstimate>() {

    @Override
    public FacilityDemographicEstimate instantiate(PropertyLookup<FacilityDemographicEstimate> lookup) {
      FacilityDemographicEstimate item = new FacilityDemographicEstimate();
      item.setFacilityId(1L);
      item.setConversionFactor(1.0);
      item.setDemographicEstimateId(1L);
      item.setValue(20L);
      return item;
    }
  };
}