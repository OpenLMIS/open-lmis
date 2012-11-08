package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;


@Data
@NoArgsConstructor
public class Product implements Importable {

    @ImportField(mandatory = true)
    private String code;

    @ImportField
    private String alternateItemCode;

    @ImportField
    private String manufacturer;

    @ImportField
    private String manufacturerCode;

    @ImportField
    private String manufacturerBarCode;

    @ImportField
    private String mohBarCode;

    @ImportField
    private String gtin;

    @ImportField
    private String type;

    @ImportField(mandatory = true)
    private String primaryName;

    @ImportField
    private String fullName;

    @ImportField
    private String genericName;

    @ImportField
    private String alternateName;

    @ImportField
    private String description;

    @ImportField
    private String strength;

    @ImportField(type = "long")
    private long form;

    @ImportField(type = "long")
    private long dosageUnit;

    @ImportField
    private String dispensingUnit;

    @ImportField(type = "int")
    private int dosesPerDispensingUnit;

    @ImportField(type = "int")
    private int dosesPerDay;


    @ImportField(type = "boolean")
    private boolean storeRefrigerated;

    @ImportField(type = "boolean")
    private boolean storeRoomTemperature;

    @ImportField(type = "boolean")
    private boolean hazardous;

    @ImportField(type = "boolean")
    private boolean flammable;

    @ImportField(type = "boolean")
    private boolean controlledSubstance;

    @ImportField(type = "boolean")
    private boolean lightSensitive;

    @ImportField(type = "boolean")
    private boolean approvedByWHO;

    @ImportField(type = "Double")
    private Double contraceptiveCYP;

    @ImportField(mandatory = true, type = "int")
    private int packSize;

    @ImportField(type = "int")
    private int alternatePackSize;

    @ImportField(type = "Double")
    private Double packLength;

    @ImportField(type = "Double")
    private Double packWidth;

    @ImportField(type = "Double")
    private Double packHeight;

    @ImportField(type = "Double")
    private Double packWeight;

    @ImportField(type = "int")
    private int packsPerCarton;

    @ImportField(type = "Double")
    private Double cartonLength;

    @ImportField(type = "Double")
    private Double cartonWidth;

    @ImportField(type = "Double")
    private Double cartonHeight;

    @ImportField(type = "int")
    private int cartonsPerPallet;

    @ImportField(type = "int")
    private int expectedShelfLife;

    @ImportField
    private String specialStorageInstructions;

    @ImportField
    private String specialTransportInstructions;

    @ImportField(mandatory = true, type = "boolean")
    private boolean active;

    @ImportField(mandatory = true, type = "boolean")
    private boolean fullSupply;

    @ImportField(mandatory = true, type = "boolean")
    private boolean tracer;

    @ImportField(mandatory = true, type = "int")
    private int packRoundingThreshold;

    @ImportField(mandatory = true, type = "boolean")
    private boolean roundToZero;

    @ImportField(type = "boolean")
    private boolean archived;

    private Date lastModifiedDate;

    private long lastModifiedBy;

    @Override
    public boolean validate() {
        return true;
    }
}
