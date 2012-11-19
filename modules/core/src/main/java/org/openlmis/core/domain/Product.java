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

    @ImportField(type = "double")
    private Double contraceptiveCYP;

    @ImportField(mandatory = true, type = "int")
    private int packSize;

    @ImportField(type = "int")
    private int alternatePackSize;

    @ImportField(type = "double")
    private Double packLength;

    @ImportField(type = "double")
    private Double packWidth;

    @ImportField(type = "double")
    private Double packHeight;

    @ImportField(type = "double")
    private Double packWeight;

    @ImportField(type = "int")
    private int packsPerCarton;

    @ImportField(type = "double")
    private Double cartonLength;

    @ImportField(type = "double")
    private Double cartonWidth;

    @ImportField(type = "double")
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

    private Date modifiedDate;

    private long modifiedBy;
}
