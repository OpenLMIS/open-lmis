package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.openlmis.core.domain.Product;

public interface ProductMapper {

    @Insert("INSERT INTO Product (" +
            "code, " +
            "alternate_item_code," +
            "manufacturer," + "manufacturer_code," + "manufacturer_barcode," +
            "moh_barcode," +
            "gtin," +
            "type," +
            "primary_name," + "full_name," + "generic_name," + "alternate_name," + "description," +
            "strength," +
            "form," +
            "dosage_unit, dispensing_unit, doses_per_dispensing_unit, doses_per_day," +
            "pack_size," + "alternate_pack_size," +
            "store_refrigerated," + "store_room_temperature," + "hazardous," + "flammable," + "controlled_substance," + "light_sensitive," + "approved_by_who," +
            "contraceptive_cyp," +
            "pack_length," + "pack_width," + "pack_height," + "pack_weight," + "packs_per_carton," +
            "carton_length," + "carton_width," + "carton_height," + "cartons_per_pallet," +
            "expected_shelf_life," +
            "special_storage_instructions," + "special_transport_instructions," +
            "active," + "full_supply," + "tracer," + "round_to_zero," + "archived," +
            "pack_rounding_threshold," +
            "modified_by)" +

            "VALUES(" +
            "#{code}," +
            "#{alternateItemCode}," +
            "#{manufacturer}," + "#{manufacturerCode}," + "#{manufacturerBarCode}," +
            "#{mohBarCode}," +
            "#{gtin}," +
            "#{type}," +
            "#{primaryName}," + "#{fullName}," + "#{genericName}," + "#{alternateName}," + "#{description}," +
            "#{strength}," +
            "#{form}, " +
            "#{dosageUnit}, #{dispensingUnit}, #{dosesPerDispensingUnit}, #{dosesPerDay}," +
            "#{packSize}," + "#{alternatePackSize}," +
            "#{storeRefrigerated}," + "#{storeRoomTemperature}," + "#{hazardous}," + "#{flammable}," + "#{controlledSubstance}," + "#{lightSensitive}," + "#{approvedByWHO}," +
            "#{contraceptiveCYP}," +
            "#{packLength}," + "#{packWidth}," + "#{packHeight}," + "#{packWeight}," + "#{packsPerCarton}," +
            "#{cartonLength}," + "#{cartonWidth}," + "#{cartonHeight}," + "#{cartonsPerPallet}," +
            "#{expectedShelfLife}," +
            "#{specialStorageInstructions}," + "#{specialTransportInstructions}," +
            "#{active}," + "#{fullSupply}," + "#{tracer}," + "#{roundToZero}," + "#{archived}," +
            "#{packRoundingThreshold}," +
            "#{modifiedBy})")
    int insert(Product product);
}
