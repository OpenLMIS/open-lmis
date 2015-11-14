/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * Product represents real world entity Product. It also defines the contract for creation/upload of product entity.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
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

  private Long formId;

  @ImportField(type = "String", name = "Product Group", nested = "code")
  private ProductGroup productGroup;

  private Long productGroupId;

  @ImportField(type = "String", name = "Dosage Units", nested = "code")
  private DosageUnit dosageUnit;

  private Long dosageUnitId;

  @ImportField(mandatory = true, name = "Dispensing Units")
  private String dispensingUnit;

  @ImportField(mandatory = true, type = "int", name = "Doses Per Dispensing Unit")
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

  public void validate() {
    if (this.packSize <= 0) {
      throw new DataException("error.invalid.pack.size");
    }
  }

  @JsonIgnore
  public String getName() {
    return (getPrimaryName() == null ? "" : getPrimaryName())
      + " " + (getForm() == null || getForm().getCode() == null ? "" : getForm().getCode())
      + " " + (getStrength() == null ? "" : getStrength())
      + " " + (getDosageUnit() == null || getDosageUnit().getCode() == null ? "" : getDosageUnit().getCode());
  }

}
