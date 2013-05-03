/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.domain.ProductForm;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProductBuilder {

  public static final String PRODUCT_CODE = "P999";

  public static final Property<Product, String> code = newProperty();
  public static final Property<Product, Boolean> fullSupply = newProperty();
  public static final Property<Product, Integer> displayOrder = newProperty();
  public static final Property<Product, Integer> productCategoryDisplayOrder = newProperty();
  public static final Property<Product, String> productCategoryCode = newProperty();
  public static final Property<Product, String> productCategoryName = newProperty();

  private static final Integer nullInteger = null;
  public static final String CATEGORY_CODE = "C1";
  public static final String CATEGORY_NAME = "Category 1";
  public static final Integer CATEGORY_DISPLAY_ORDER = 1;

  public static final Instantiator<Product> defaultProduct = new Instantiator<Product>() {
    @Override
    public Product instantiate(PropertyLookup<Product> lookup) {
      Product product = new Product();

      product.setCode(lookup.valueOf(code, PRODUCT_CODE));
      product.setFullSupply(lookup.valueOf(fullSupply, true));
      product.setActive(true);
      product.setAlternateItemCode("alternateItemCode");
      product.setManufacturer("Glaxo and Smith");
      product.setManufacturerCode("manufacturerCode");
      product.setManufacturerBarCode("manufacturerBarCode");
      product.setMohBarCode("mohBarCode");
      product.setGtin("gtin");
      product.setType("antibiotic");
      product.setPrimaryName("Primary Name");
      product.setFullName("TDF/FTC/EFV");
      product.setDisplayOrder(lookup.valueOf(displayOrder, nullInteger));
      product.setGenericName("Generic - TDF/FTC/EFV");
      product.setAlternateName("Alt - TDF/FTC/EFV");
      product.setDescription("is a med");
      product.setStrength("strength");
      DosageUnit dosageUnit = new DosageUnit();
      dosageUnit.setCode("mg");
      dosageUnit.setId(1L);
      product.setDosageUnit(dosageUnit);
      product.setDispensingUnit("Strip");
      product.setPackSize(10);
      product.setTracer(true);
      product.setPackRoundingThreshold(1);
      product.setRoundToZero(true);
      product.setDosesPerDispensingUnit(10);
      ProductForm form = new ProductForm();
      form.setCode("Tablet");
      form.setId(1L);
      product.setForm(form);

      ProductCategory category = new ProductCategory();
      category.setCode(lookup.valueOf(productCategoryCode, CATEGORY_CODE));
      category.setName(lookup.valueOf(productCategoryName, CATEGORY_NAME));
      category.setDisplayOrder(lookup.valueOf(productCategoryDisplayOrder, CATEGORY_DISPLAY_ORDER));
      product.setCategory(category);

      return product;
    }
  };

}
