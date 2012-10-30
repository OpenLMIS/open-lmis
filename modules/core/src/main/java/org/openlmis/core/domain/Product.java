package org.openlmis.core.domain;

import java.util.Date;

public class Product {

    private String productCode;
    private String alternateItemCode;
    private long productManufacturerId;
    private String manufacturerProductCode;
    private String manufacturerBarCode;
    private String moHBarCode;
    private String GTIN;
    private long productType;
    private String productPrimaryName;
    private String productFullName;
    private String genericName;
    private String alternateName;
    private String description;
    private String productStrength;
    private long productForm;
    private long dosageUnits;
    private long dispensingUnits;
    private int dosesPerDispensingUnit;
    private int packSize;
    private int alternatePackSize;
    private boolean storeRefrigerated;
    private boolean storeRoomTemperature;
    private boolean productIsHazardous;
    private boolean productIsFlammable;
    private boolean productIsControlledSubstance;
    private boolean productIsLightSensitive;
    private boolean approvedByWHO;
    private String contraceptiveCYP;
    private String packLength;
    private String packWidth;
    private String packHeight;
    private String packWeight;
    private int packsPerCarton;
    private String cartonLength;
    private String cartonWidth;
    private String cartonHeight;
    private int cartonsPerPallet;
    private int expectedShelfLife;
    private String specialStorageInstructions;
    private String specialTransportInstructions;
    private boolean productIsActive;
    private boolean productIsFullSupply;
    private boolean isATracerProduct;
    private int packRoundingThreshold;
    private boolean canRoundToZero;
    private boolean hasBeenArchived;
    private Date lastModifiedDate;
    private long lastModifiedBy;

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setAlternateItemCode(String alternateItemCode) {
        this.alternateItemCode = alternateItemCode;
    }

    public void setProductManufacturerId(long productManufacturerId) {
        this.productManufacturerId = productManufacturerId;
    }

    public void setManufacturerProductCode(String manufacturerProductCode) {
        this.manufacturerProductCode = manufacturerProductCode;
    }

    public void setManufacturerBarCode(String manufacturerBarCode) {
        this.manufacturerBarCode = manufacturerBarCode;
    }

    public void setMoHBarCode(String moHBarCode) {
        this.moHBarCode = moHBarCode;
    }

    public void setGTIN(String GTIN) {
        this.GTIN = GTIN;
    }

    public void setProductType(long productType) {
        this.productType = productType;
    }

    public void setProductPrimaryName(String productPrimaryName) {
        this.productPrimaryName = productPrimaryName;
    }

    public void setProductFullName(String productFullName) {
        this.productFullName = productFullName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public void setAlternateName(String alternateName) {
        this.alternateName = alternateName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProductStrength(String productStrength) {
        this.productStrength = productStrength;
    }

    public void setProductForm(long productForm) {
        this.productForm = productForm;
    }

    public void setDosageUnits(long dosageUnits) {
        this.dosageUnits = dosageUnits;
    }

    public void setDispensingUnits(long dispensingUnits) {
        this.dispensingUnits = dispensingUnits;
    }

    public void setDosesPerDispensingUnit(int dosesPerDispensingUnit) {
        this.dosesPerDispensingUnit = dosesPerDispensingUnit;
    }

    public void setPackSize(int packSize) {
        this.packSize = packSize;
    }

    public void setAlternatePackSize(int alternatePackSize) {
        this.alternatePackSize = alternatePackSize;
    }

    public void setStoreRefrigerated(boolean storeRefrigerated) {
        this.storeRefrigerated = storeRefrigerated;
    }

    public void setStoreRoomTemperature(boolean storeRoomTemperature) {
        this.storeRoomTemperature = storeRoomTemperature;
    }

    public void setProductIsHazardous(boolean productIsHazardous) {
        this.productIsHazardous = productIsHazardous;
    }

    public void setProductIsFlammable(boolean productIsFlammable) {
        this.productIsFlammable = productIsFlammable;
    }

    public void setProductIsControlledSubstance(boolean productIsControlledSubstance) {
        this.productIsControlledSubstance = productIsControlledSubstance;
    }

    public void setProductIsLightSensitive(boolean productIsLightSensitive) {
        this.productIsLightSensitive = productIsLightSensitive;
    }

    public void setApprovedByWHO(boolean approvedByWHO) {
        this.approvedByWHO = approvedByWHO;
    }

    public void setContraceptiveCYP(String contraceptiveCYP) {
        this.contraceptiveCYP = contraceptiveCYP;
    }

    public void setPackLength(String packLength) {
        this.packLength = packLength;
    }

    public void setPackWidth(String packWidth) {
        this.packWidth = packWidth;
    }

    public void setPackHeight(String packHeight) {
        this.packHeight = packHeight;
    }

    public void setPackWeight(String packWeight) {
        this.packWeight = packWeight;
    }

    public void setPacksPerCarton(int packsPerCarton) {
        this.packsPerCarton = packsPerCarton;
    }

    public void setCartonLength(String cartonLength) {
        this.cartonLength = cartonLength;
    }

    public void setCartonWidth(String cartonWidth) {
        this.cartonWidth = cartonWidth;
    }

    public void setCartonHeight(String cartonHeight) {
        this.cartonHeight = cartonHeight;
    }

    public void setCartonsPerPallet(int cartonsPerPallet) {
        this.cartonsPerPallet = cartonsPerPallet;
    }

    public void setExpectedShelfLife(int expectedShelfLife) {
        this.expectedShelfLife = expectedShelfLife;
    }

    public void setSpecialStorageInstructions(String specialStorageInstructions) {
        this.specialStorageInstructions = specialStorageInstructions;
    }

    public void setSpecialTransportInstructions(String specialTransportInstructions) {
        this.specialTransportInstructions = specialTransportInstructions;
    }

    public void setProductIsActive(boolean productIsActive) {
        this.productIsActive = productIsActive;
    }

    public void setProductIsFullSupply(boolean productIsFullSupply) {
        this.productIsFullSupply = productIsFullSupply;
    }

    public void setIsATracerProduct(boolean ATracerProduct) {
        isATracerProduct = ATracerProduct;
    }

    public void setPackRoundingThreshold(int packRoundingThreshold) {
        this.packRoundingThreshold = packRoundingThreshold;
    }

    public void setCanRoundToZero(boolean canRoundToZero) {
        this.canRoundToZero = canRoundToZero;
    }

    public void setHasBeenArchived(boolean hasBeenArchived) {
        this.hasBeenArchived = hasBeenArchived;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public void setLastModifiedBy(long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getProductCode() {
        return productCode;
    }
}
