/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.*;

import static com.natpryce.makeiteasy.Property.newProperty;

public class FacilityApprovedProductBuilder {

  public static final Property<FacilityApprovedProduct, String> facilityTypeCode = newProperty();
  public static final Property<FacilityApprovedProduct, Integer> facilityTypeId = newProperty();
  public static final Property<FacilityApprovedProduct, String> programCode = newProperty();
  public static final Property<FacilityApprovedProduct, String> productCode = newProperty();
  public static final Property<FacilityApprovedProduct, Integer> maxMonthsOfStock = newProperty();

  public static final String defaultFacilityTypeCode = "code";
  public static final Integer defaultFacilityTypeId = 1;
  public static final String defaultProgramCode = "YELL_FVR";
  public static final String defaultProductCode = "P999";
  public static final Integer defaultMaxMonthsOfStock = 3;

  public static final Instantiator<FacilityApprovedProduct> defaultFacilityApprovedProduct = new Instantiator<FacilityApprovedProduct>() {

    @Override
    public FacilityApprovedProduct instantiate(PropertyLookup<FacilityApprovedProduct> lookup) {

      Program program = new Program();
      program.setCode(lookup.valueOf(programCode, defaultProgramCode));

      Product product = new Product();
      product.setCode(lookup.valueOf(productCode, defaultProductCode));

      ProgramProduct programProduct = new ProgramProduct(program, product, 1, true);

      FacilityType facilityType = new FacilityType(lookup.valueOf(facilityTypeCode, defaultFacilityTypeCode));
      facilityType.setId(lookup.valueOf(facilityTypeId, defaultFacilityTypeId));
      return new FacilityApprovedProduct(
        facilityType,
        programProduct,
        lookup.valueOf(maxMonthsOfStock, defaultMaxMonthsOfStock));

    }
  };
}