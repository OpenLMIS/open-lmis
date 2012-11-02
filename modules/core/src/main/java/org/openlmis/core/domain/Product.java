package org.openlmis.core.domain;

import lombok.Getter;
import lombok.Setter;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

public class Product implements Importable {

    @Getter
    @Setter
    @ImportField(mandatory = true)
    private String productCode;

    @Getter
    @Setter
    @ImportField
    private String alternateItemCode;

    @Getter
    @Setter
    @ImportField
    private long productManufacturerId;

    @Getter
    @Setter
    @ImportField
    private String manufacturerProductCode;

    @Getter
    @Setter
    @ImportField
    private String manufacturerBarCode;

    @Getter
    @Setter
    @ImportField
    private String moHBarCode;

    @Getter
    @Setter
    @ImportField
    private String gtin;

    @Getter
    @Setter
    @ImportField(type = "long")
    private long productType;

    @Getter
    @Setter
    @ImportField(mandatory = true)
    private String productPrimaryName;

    @Getter
    @Setter
    @ImportField
    private String productFullName;

    @Getter
    @Setter
    @ImportField
    private String genericName;

    @Getter
    @Setter
    @ImportField
    private String alternateName;

    @Getter
    @Setter
    @ImportField
    private String description;

    @Getter
    @Setter
    @ImportField
    private String productStrength;

//    @Getter
//    @Setter
//    private long productForm;

//    @Getter
//    @Setter
//    private long dosageUnits;

    @Getter
    @Setter
    @ImportField
    private String dispensingUnits;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int dosesPerDispensingUnit;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int dosesPerDay;


    @Getter
    @Setter
    @ImportField(type = "boolean")
    private boolean storeRefrigerated;

    @Getter
    @Setter
    @ImportField(type = "boolean")
    private boolean storeRoomTemperature;

    @Getter
    @Setter
    @ImportField(type = "boolean")
    private boolean hazardous;

    @Getter
    @Setter
    @ImportField(type = "boolean")
    private boolean flammable;

    @Getter
    @Setter
    @ImportField(type = "boolean")
    private boolean controlledSubstance;

    @Getter
    @Setter
    @ImportField(type = "boolean")
    private boolean sensitive;

    @Getter
    @Setter
    @ImportField(type = "boolean")
    private boolean approvedByWHO;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int contraceptiveCYP;

    @Getter
    @Setter
    @ImportField(mandatory = true, type = "int")
    private int packSize;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int alternatePackSize;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int packLength;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int packWidth;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int packHeight;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int packWeight;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int packsPerCarton;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int cartonLength;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int cartonWidth;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int cartonHeight;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int cartonsPerPallet;

    @Getter
    @Setter
    @ImportField(type = "int")
    private int expectedShelfLife;

    @Getter
    @Setter
    @ImportField
    private String specialStorageInstructions;

    @Getter
    @Setter
    @ImportField
    private String specialTransportInstructions;

    @Getter
    @Setter
    @ImportField(mandatory = true, type = "boolean")
    private boolean active;

    @Getter
    @Setter
    @ImportField(mandatory = true, type = "boolean")
    private boolean fullSupply;

    @Getter
    @Setter
    @ImportField(mandatory = true, type = "boolean")
    private boolean tracerProduct;

    @Getter
    @Setter
    @ImportField(mandatory = true, type = "int")
    private int packRoundingThreshold;

    @Getter
    @Setter
    @ImportField(mandatory = true, type = "boolean")
    private boolean roundToZero;

    @Getter
    @Setter
    @ImportField(type = "boolean")
    private boolean archived;

    @Getter
    @Setter
    private Date lastModifiedDate;

    @Getter
    @Setter
    private long lastModifiedBy;

    @Override
    public boolean validate() {
        return true;
    }
}
