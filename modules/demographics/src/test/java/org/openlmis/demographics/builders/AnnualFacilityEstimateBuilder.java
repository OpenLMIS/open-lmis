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
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;

import static com.natpryce.makeiteasy.Property.newProperty;

public class AnnualFacilityEstimateBuilder {

  public static final Property<AnnualFacilityEstimateEntry, Long> id = newProperty();
  public static final Property<AnnualFacilityEstimateEntry, Long> categoryId = newProperty();
  public static final Property<AnnualFacilityEstimateEntry, Long> facilityId = newProperty();
  public static final Property<AnnualFacilityEstimateEntry, Integer> year = newProperty();
  public static final Property<AnnualFacilityEstimateEntry, Long> value = newProperty();
  public static final Property<AnnualFacilityEstimateEntry, Boolean> isFinal = newProperty();


  public static Long nullValue = null;

  public static final Instantiator<AnnualFacilityEstimateEntry> defaultAnnualFacilityEstimateEntry = new Instantiator<AnnualFacilityEstimateEntry>() {

    @Override
    public AnnualFacilityEstimateEntry instantiate(PropertyLookup<AnnualFacilityEstimateEntry> lookup) {
      AnnualFacilityEstimateEntry item = new AnnualFacilityEstimateEntry();
      item.setId(lookup.valueOf(id, nullValue));
      item.setYear(lookup.valueOf(year, 2015));
      item.setIsFinal(lookup.valueOf(isFinal, false));
      item.setDemographicEstimateId(lookup.valueOf(categoryId, 1L));
      item.setFacilityId(lookup.valueOf(facilityId, 1L));
      item.setConversionFactor(1.0);
      item.setValue(lookup.valueOf(value, 10000L));
      return item;
    }
  };
}