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

package org.openlmis.equipment.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.equipment.domain.Donor;

import static com.natpryce.makeiteasy.Property.newProperty;

public class DonorBuilder {

  public static final Property<Donor, Long> id = newProperty();
  public static final Property<Donor, String> code = newProperty();
  public static final Property<Donor, String> shortName = newProperty();
  public static final Property<Donor, String> longName = newProperty();
  public static final Property<Donor, Long> modifiedBy = newProperty();

  public static final String SHORT_NAME = "USAID";
  public static final String CODE = "USAID";
  public static final String LONG_NAME = "United States Agency For International Development";
  public static final Long SCHEDULE_MODIFIED_BY = 1L;

  public static final Instantiator<Donor> defaultDonor = new Instantiator<Donor>() {

    @Override
    public Donor instantiate(PropertyLookup<Donor> lookup) {
      Donor donor = new Donor();
      donor.setCode(lookup.valueOf(code, CODE));
      donor.setShortName(lookup.valueOf(shortName, SHORT_NAME));
      donor.setLongName(lookup.valueOf(longName, LONG_NAME));
      return donor;
    }
  };
}
