/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.*;

import static com.natpryce.makeiteasy.Property.newProperty;

public class FacilityApprovedProductBuilder {

  public static final Property<FacilityTypeApprovedProduct, String> facilityTypeCode = newProperty();
  public static final Property<FacilityTypeApprovedProduct, Long> facilityTypeId = newProperty();
  public static final Property<FacilityTypeApprovedProduct, String> programCode = newProperty();
  public static final Property<FacilityTypeApprovedProduct, String> productCode = newProperty();
  public static final Property<FacilityTypeApprovedProduct, Double> maxMonthsOfStock = newProperty();

  public static final String defaultFacilityTypeCode = "code";
  public static final Long defaultFacilityTypeId = 1L;
  public static final String defaultProgramCode = "YELL_FVR";
  public static final String defaultProductCode = "P999";
  public static final Double defaultMaxMonthsOfStock = 3.0;

  public static final Instantiator<FacilityTypeApprovedProduct> defaultFacilityApprovedProduct = new Instantiator<FacilityTypeApprovedProduct>() {

    @Override
    public FacilityTypeApprovedProduct instantiate(PropertyLookup<FacilityTypeApprovedProduct> lookup) {

      Program program = new Program();
      program.setCode(lookup.valueOf(programCode, defaultProgramCode));

      Product product = new Product();
      product.setCode(lookup.valueOf(productCode, defaultProductCode));
      product.setFullSupply(true);

      ProgramProduct programProduct = new ProgramProduct(program, product, 1, true);
      programProduct.setFullSupply(product.getFullSupply());

      FacilityType facilityType = new FacilityType(lookup.valueOf(facilityTypeCode, defaultFacilityTypeCode));
      facilityType.setId(lookup.valueOf(facilityTypeId, defaultFacilityTypeId));
      return new FacilityTypeApprovedProduct(
        facilityType,
        programProduct,
        lookup.valueOf(maxMonthsOfStock, defaultMaxMonthsOfStock));

    }
  };
}