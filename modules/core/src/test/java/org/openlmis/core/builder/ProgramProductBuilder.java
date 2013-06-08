/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProgramProductBuilder {

  public static final Property<ProgramProduct, Long> programId = newProperty();
  public static final Property<ProgramProduct, Long> productId = newProperty();
  public static final Property<ProgramProduct, Integer> dosagePerMonth = newProperty();

  private static Property<ProgramProduct, String> productCode = newProperty();
  private static Property<ProgramProduct, String> programCode = newProperty();
  public static final String PRODUCT_CODE = "productCode";
  public static final String PROGRAM_CODE = "programCode";
  public static final Instantiator<ProgramProduct> defaultProgramProduct = new Instantiator<ProgramProduct>() {

    @Override
    public ProgramProduct instantiate(PropertyLookup<ProgramProduct> lookup) {
      Product product = new Product();
      product.setId(lookup.valueOf(productId, 1L));
      product.setCode(lookup.valueOf(productCode, PRODUCT_CODE));
      Program program = new Program();
      program.setId(lookup.valueOf(programId, 1L));
      program.setCode(lookup.valueOf(programCode, PROGRAM_CODE));
      return new ProgramProduct(program, product, lookup.valueOf(dosagePerMonth, 1), true, new Money("100"));
    }
  };
}
