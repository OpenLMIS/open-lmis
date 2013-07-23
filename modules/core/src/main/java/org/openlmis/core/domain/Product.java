/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class Product extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "Product Code")
  private String code;

  @ImportField(name = "Alternate Item Code")
  private String alternateItemCode;

  @ImportField(name = "Product Manufacturer ID")
  private String manufacturer;

  @ImportField(name = "Manufacturer Product Code")
  private String manufacturerCode;

  @ImportField(name = "Manufacturer Bar Code")
  private String manufacturerBarCode;

  @ImportField(name = "MoH Bar Code")
  private String mohBarCode;

  @ImportField(name = "GTIN")
  private String gtin;

  @ImportField(name = "Product Type")
  private String type;

  @ImportField(name = "Display Order", type = "int")
  private Integer displayOrder;

  @ImportField(mandatory = true, name = "Product Primary Name")
  private String primaryName;

  @ImportField(name = "Product Full Name")
  private String fullName;

  @ImportField(name = "Generic Name")
  private String genericName;

  @ImportField(name = "Alternate Name")
  private String alternateName;

  @ImportField(name = "Description")
  private String description;

  @ImportField(name = "Product Strength")
  private String strength;

  @ImportField(type = "String", name = "Product Form", nested = "code")
  private ProductForm form;

  @ImportField(mandatory = true, type = "String", name = "Product Category", nested = "code")
  private ProductCategory category;

  @ImportField(type = "String", name = "Product Group", nested = "code")
  private ProductGroup productGroup;

  @ImportField(type = "String", name = "Dosage Units", nested = "code")
  private DosageUnit dosageUnit;

  @ImportField(name = "Dispensing Units")
  private String dispensingUnit;

  @ImportField(type = "int", name = "Doses Per Dispensing Unit")
  private Integer dosesPerDispensingUnit;

  @ImportField(type = "boolean", name = "Store Refrigerated")
  private Boolean storeRefrigerated;

  @ImportField(type = "boolean", name = "Store Room Temperature")
  private Boolean storeRoomTemperature;

  @ImportField(type = "boolean", name = "Product Is Hazardous")
  private Boolean hazardous;

  @ImportField(type = "boolean", name = "Product Is Flammable")
  private Boolean flammable;

  @ImportField(type = "boolean", name = "Product Is Controlled Substance")
  private Boolean controlledSubstance;

  @ImportField(type = "boolean", name = "Product Is Light Sensitive")
  private Boolean lightSensitive;

  @ImportField(type = "boolean", name = "Approved By WHO")
  private Boolean approvedByWHO;

  @ImportField(type = "double", name = "Contraceptive CYP")
  private Double contraceptiveCYP;

  @ImportField(mandatory = true, type = "int", name = "Pack Size")
  private Integer packSize;

  @ImportField(type = "int", name = "Alternate Pack Size")
  private Integer alternatePackSize;

  @ImportField(type = "double", name = "Pack Length")
  private Double packLength;

  @ImportField(type = "double", name = "Pack Width")
  private Double packWidth;

  @ImportField(type = "double", name = "Pack Height")
  private Double packHeight;

  @ImportField(type = "double", name = "Pack Weight")
  private Double packWeight;

  @ImportField(type = "int", name = "Packs Per Carton")
  private Integer packsPerCarton;

  @ImportField(type = "double", name = "Carton Length")
  private Double cartonLength;

  @ImportField(type = "double", name = "Carton Width")
  private Double cartonWidth;

  @ImportField(type = "double", name = "Carton Height")
  private Double cartonHeight;

  @ImportField(type = "int", name = "Cartons Per Pallet")
  private Integer cartonsPerPallet;

  @ImportField(type = "int", name = "Expected Shelf Life")
  private Integer expectedShelfLife;

  @ImportField(name = "Special Storage Instructions")
  private String specialStorageInstructions;

  @ImportField(name = "Special Transport Instructions")
  private String specialTransportInstructions;

  @ImportField(mandatory = true, type = "boolean", name = "Product Is Active")
  private Boolean active;

  @ImportField(mandatory = true, type = "boolean", name = "Product Is Full Supply")
  private Boolean fullSupply;

  @ImportField(mandatory = true, type = "boolean", name = "Is A Tracer Product")
  private Boolean tracer;

  @ImportField(mandatory = true, type = "int", name = "Pack Rounding Threshold")
  private Integer packRoundingThreshold;

  @ImportField(mandatory = true, type = "boolean", name = "Can Round To Zero")
  private Boolean roundToZero;

  @ImportField(type = "boolean", name = "Has Been Archived")
  private Boolean archived;

}
