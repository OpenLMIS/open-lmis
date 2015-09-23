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

package org.openlmis.demographics.builders;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.demographics.domain.AnnualDistrictEstimateEntry;
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.openlmis.demographics.dto.EstimateFormLineItem;

import java.util.List;

import static com.natpryce.makeiteasy.Property.newProperty;
import static java.util.Arrays.asList;

public class EstimateFormLineItemBuilder {

  public static final Property<EstimateFormLineItem, List<AnnualFacilityEstimateEntry>> facilityAnnualEstimates = newProperty();
  public static final Property<EstimateFormLineItem, List<AnnualDistrictEstimateEntry>> districtAnnualEstimates = newProperty();


  public static final Instantiator<EstimateFormLineItem> defaultDemographicEstimateLineItem = new Instantiator<EstimateFormLineItem>() {

    List<AnnualFacilityEstimateEntry> facilityEstimateEntries = asList(new AnnualFacilityEstimateEntry());
    List<AnnualDistrictEstimateEntry> districtEstimateEntries = asList(new AnnualDistrictEstimateEntry());

    @Override
    public EstimateFormLineItem instantiate(PropertyLookup<EstimateFormLineItem> lookup) {
      EstimateFormLineItem item = new EstimateFormLineItem();
      item.setFacilityEstimates(lookup.valueOf(facilityAnnualEstimates, facilityEstimateEntries));
      item.setDistrictEstimates(lookup.valueOf(districtAnnualEstimates, districtEstimateEntries));
      return item;
    }
  };
}
