/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.ProcessingSchedule;
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
