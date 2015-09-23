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
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.domain.ProductGroup;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProductBuilder {

  public static final Long PRODUCT_ID = null;
  public static final String PRODUCT_CODE = "P999";
  public static final String PRIMARY_NAME = "Primary Name";

  public static final Property<Product, Long> productId = newProperty();
  public static final Property<Product, String> code = newProperty();
  public static final Property<Product, String> primaryName = newProperty();
  public static final Property<Product, Boolean> fullSupply = newProperty();
  public static final Property<Product, Boolean> active = newProperty();

  public static final Instantiator<Product> defaultProduct = new Instantiator<Product>() {
    @Override
    public Product instantiate(PropertyLookup<Product> lookup) {
      Product product = new Product();

      product.setId(lookup.valueOf(productId, PRODUCT_ID));
      product.setCode(lookup.valueOf(code, PRODUCT_CODE));
      product.setFullSupply(lookup.valueOf(fullSupply, true));
      product.setActive(lookup.valueOf(active, true));
      product.setPrimaryName(lookup.valueOf(primaryName, PRIMARY_NAME));
      product.setAlternateItemCode("alternateItemCode");
      product.setManufacturer("Glaxo and Smith");
      product.setManufacturerCode("manufacturerCode");
      product.setManufacturerBarCode("manufacturerBarCode");
      product.setMohBarCode("mohBarCode");
      product.setGtin("gtin");
      product.setType("antibiotic");
      product.setFullName("TDF/FTC/EFV");
      product.setGenericName("Generic - TDF/FTC/EFV");
      product.setAlternateName("Alt - TDF/FTC/EFV");
      product.setDescription("is a med");
      product.setStrength("strength");
      DosageUnit dosageUnit = new DosageUnit("mg", 1);
      dosageUnit.setId(1L);
      product.setDosageUnit(dosageUnit);
      product.setDispensingUnit("Strip");
      product.setPackSize(10);
      product.setTracer(true);
      product.setPackRoundingThreshold(1);
      product.setRoundToZero(true);
      product.setDosesPerDispensingUnit(10);
      ProductForm form = new ProductForm();
      form.setDisplayOrder(1);
      form.setCode("Tablet");
      form.setId(1L);
      product.setForm(form);
      ProductGroup productGroup = new ProductGroup();
      productGroup.setCode("PG");
      productGroup.setName("Product Group 1");
      productGroup.setId(1L);
      product.setProductGroup(productGroup);
      return product;
    }
  };

}
