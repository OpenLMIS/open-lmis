package org.openlmis.utils.csv;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class CsvCellProcessor {
    public static CellProcessor[] getProductProcessors() {
        CellProcessor[] cellProcessor = new CellProcessor[]{
                new Unique(new NotNull()),      //ProductCode
                null,                           //AlternateItemCode
                null,                           //ProductManufacturerID
                null,                           //ManufacturerProductCode
                null,                           //ManufacturerBarCode
                new Optional(new ParseLong()),  //MoHBarCode
                null,                           //GTIN
                new Optional(new ParseLong()),  //ProductType
                null,                           //ProductPrimaryName
                null,                           //ProductFullName
                null,                           //GenericName
                null,                           //AlternateName
                new Optional(),                 //Description
                new Optional(),                 //ProductStrength
                new Optional(new ParseLong()),  //ProductForm
                new Optional(new ParseLong()),  //DosageUnits
                new Optional(new ParseLong()),  //DispensingUnits
                null,                           //DosesPerDispensingUnit
                new Optional(new ParseInt()),   //PackSize
                null,                           //AlternatePackSize
                null,                           //StoreRefrigerated
                null,                           //StoreRoomTemperature
                null,                           //ProductIsHazardous
                null,                           //ProductIsFlammable
                null,                           //ProductIsControlledSubstance
                null,                           //ProductIsLightSensitive
                null,                           //ApprovedByWHO
                null,                           //ContraceptiveCYP
                null,                           //PackLength
                null,                           //PackWidth
                null,                           //PackHeight
                null,                           //PackWeight
                null,                           //PacksPerCarton
                null,                           //CartonLength
                null,                           //CartonWidth
                null,                           //CartonHeight
                null,                           //CartonsPerPallet
                null,                           // ExpectedShelfLife
                null,                           //SpecialStorageInstructions
                null,                           //SpecialTransportInstructions
                new Optional(new ParseBool()),  //ProductIsActive
                null,                           //ProductIsFullSupply
                new Optional(new ParseBool()),  //IsATracerProduct
                new Optional(new ParseInt()),   //PackRoundingThreshold
                new Optional(new ParseBool()),  //CanRoundToZero
                null,                           //HasBeenArchived
                null,                           //LastModifiedDate
                null                            //LastModifiedBy
        };
        return cellProcessor;
    }

}
