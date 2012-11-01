package org.openlmis.core.domain;

import lombok.Getter;
import lombok.Setter;
import org.openlmis.upload.Importable;

import java.util.Date;

public class Product implements Importable {


    @Getter
    @Setter
    private String productCode;

    @Getter
    @Setter
    private String alternateItemCode;

    @Getter
    @Setter
    private long productManufacturerId;

    @Getter
    @Setter
    private String manufacturerProductCode;

    @Getter
    @Setter
    private String manufacturerBarCode;

    @Getter
    @Setter
    private String moHBarCode;

    @Getter
    @Setter
    private String GTIN;

    @Getter
    @Setter
    private long productType;

    @Getter
    @Setter
    private String productPrimaryName;

    @Getter
    @Setter
    private String productFullName;

    @Getter
    @Setter
    private String genericName;

    @Getter
    @Setter
    private String alternateName;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String productStrength;

    @Getter
    @Setter
    private long productForm;

    @Getter
    @Setter
    private long dosageUnits;

    @Getter
    @Setter
    private long dispensingUnits;

    @Getter
    @Setter
    private int dosesPerDispensingUnit;

    @Getter
    @Setter
    private int packSize;

    @Getter
    @Setter
    private int alternatePackSize;

    @Getter
    @Setter
    private boolean storeRefrigerated;

    @Getter
    @Setter
    private boolean storeRoomTemperature;

    @Getter
    @Setter
    private boolean productIsHazardous;

    @Getter
    @Setter
    private boolean productIsFlammable;

    @Getter
    @Setter
    private boolean productIsControlledSubstance;

    @Getter
    @Setter
    private boolean productIsLightSensitive;

    @Getter
    @Setter
    private boolean approvedByWHO;

    @Getter
    @Setter
    private String contraceptiveCYP;

    @Getter
    @Setter
    private int packLength;

    @Getter
    @Setter
    private int packWidth;

    @Getter
    @Setter
    private int packHeight;

    @Getter
    @Setter
    private int packWeight;

    @Getter
    @Setter
    private int packsPerCarton;

    @Getter
    @Setter
    private int cartonLength;

    @Getter
    @Setter
    private int cartonWidth;

    @Getter
    @Setter
    private int cartonHeight;

    @Getter
    @Setter
    private int cartonsPerPallet;

    @Getter
    @Setter
    private int expectedShelfLife;

    @Getter
    @Setter
    private String specialStorageInstructions;

    @Getter
    @Setter
    private String specialTransportInstructions;

    @Getter
    @Setter
    private boolean productIsActive;

    @Getter
    @Setter
    private boolean productIsFullSupply;

    @Getter
    @Setter
    private boolean isATracerProduct;

    @Getter
    @Setter
    private int packRoundingThreshold;

    @Getter
    @Setter
    private boolean canRoundToZero;

    @Getter
    @Setter
    private boolean hasBeenArchived;

    @Getter
    @Setter
    private Date lastModifiedDate;

    @Getter
    @Setter
    private long lastModifiedBy;

    @Override
    public boolean validate() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
