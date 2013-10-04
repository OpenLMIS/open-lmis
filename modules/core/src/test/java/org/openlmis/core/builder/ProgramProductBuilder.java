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
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProgramProductBuilder {

  public static final Property<ProgramProduct, Long> programId = newProperty();
  public static final Property<ProgramProduct, Long> productId = newProperty();
  public static final Property<ProgramProduct, Integer> dosagePerMonth = newProperty();

  public static Property<ProgramProduct, String> productCode = newProperty();
  public static Property<ProgramProduct, String> programCode = newProperty();
  public static Property<ProgramProduct, Boolean> active = newProperty();
  public static Property<ProgramProduct, Boolean> productActive = newProperty();
  public static final String PRODUCT_CODE = "productCode";
  public static final String PROGRAM_CODE = "programCode";
  public static final Instantiator<ProgramProduct> defaultProgramProduct = new Instantiator<ProgramProduct>() {


    @Override
    public ProgramProduct instantiate(PropertyLookup<ProgramProduct> lookup) {
      Product product = new Product();
      product.setId(lookup.valueOf(productId, 1L));
      product.setCode(lookup.valueOf(productCode, PRODUCT_CODE));

      Boolean nullBoolean = null;
      product.setActive(lookup.valueOf(productActive, nullBoolean));
      Program program = new Program();
      program.setId(lookup.valueOf(programId, 1L));
      program.setCode(lookup.valueOf(programCode, PROGRAM_CODE));
      return new ProgramProduct(program, product, lookup.valueOf(dosagePerMonth, 1), lookup.valueOf(active, true), new Money("100"));
    }
  };
}
