package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class Product implements Importable {

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

    @ImportField(name = "Display Order")
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

    @ImportField(type = "long", name = "Product Form")
    private long form;

    @ImportField(type = "long", name = "Dosage Units")
    private long dosageUnit;

    @ImportField(name = "Dispensing Units")
    private String dispensingUnit;

    @ImportField(type = "int", name = "Doses Per Dispensing Unit")
    private int dosesPerDispensingUnit;

    @ImportField(type = "int", name = "Doses Per Day")
    private int dosesPerDay;

    @ImportField(type = "boolean", name = "Store Refrigerated")
    private boolean storeRefrigerated;

    @ImportField(type = "boolean", name = "Store Room Temperature")
    private boolean storeRoomTemperature;

    @ImportField(type = "boolean", name = "Product Is Hazardous")
    private boolean hazardous;

    @ImportField(type = "boolean", name = "Product Is Flammable")
    private boolean flammable;

    @ImportField(type = "boolean", name = "Product Is Controlled Substance")
    private boolean controlledSubstance;

    @ImportField(type = "boolean", name = "Product Is Light Sensitive")
    private boolean lightSensitive;

    @ImportField(type = "boolean", name = "Approved By WHO")
    private boolean approvedByWHO;

    @ImportField(type = "double", name = "Contraceptive CYP")
    private Double contraceptiveCYP;

    @ImportField(mandatory = true, type = "int", name = "Pack Size")
    private int packSize;

    @ImportField(type = "int", name = "Alternate Pack Size")
    private int alternatePackSize;

    @ImportField(type = "double", name = "Pack Length")
    private Double packLength;

    @ImportField(type = "double", name = "Pack Width")
    private Double packWidth;

    @ImportField(type = "double", name = "Pack Height")
    private Double packHeight;

    @ImportField(type = "double", name = "Pack Weight")
    private Double packWeight;

    @ImportField(type = "int", name = "Packs Per Carton")
    private int packsPerCarton;

    @ImportField(type = "double", name = "Carton Length")
    private Double cartonLength;

    @ImportField(type = "double", name = "Carton Width")
    private Double cartonWidth;

    @ImportField(type = "double", name = "Carton Height")
    private Double cartonHeight;

    @ImportField(type = "int", name = "Cartons Per Pallet")
    private int cartonsPerPallet;

    @ImportField(type = "int", name = "Expected Shelf Life")
    private int expectedShelfLife;

    @ImportField(name = "Special Storage Instructions")
    private String specialStorageInstructions;

    @ImportField(name = "Special Transport Instructions")
    private String specialTransportInstructions;

    @ImportField(mandatory = true, type = "boolean", name = "Product Is Active")
    private boolean active;

    @ImportField(mandatory = true, type = "boolean", name = "Product Is Full Supply")
    private boolean fullSupply;

    @ImportField(mandatory = true, type = "boolean", name = "Is A Tracer Product")
    private boolean tracer;

    @ImportField(mandatory = true, type = "int", name = "Pack Rounding Threshold")
    private int packRoundingThreshold;

    @ImportField(mandatory = true, type = "boolean", name = "Can Round To Zero")
    private boolean roundToZero;

    @ImportField(type = "boolean", name = "Has Been Archived")
    private boolean archived;

    private Date modifiedDate;

    private long modifiedBy;
}
