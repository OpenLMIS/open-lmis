package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {

    @Insert("INSERT INTO Product (" +
            "code, " +
            "alternate_item_code," +
            "manufacturer," + "manufacturer_code," + "manufacturer_barcode," +
            "moh_barcode," +
            "gtin," +
            "type," +
            "display_order," +
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
            "#{displayOrder}," +
            "#{primaryName}," + "#{fullName}," + "#{genericName}," + "#{alternateName}," + "#{description}," +
            "#{strength}," +
            "(select id from product_form where LOWER(code) =LOWER(#{formCode})), " +
            "(select id from dosage_unit where LOWER(code) =LOWER(#{dosageUnitCode}))," +
            " #{dispensingUnit}, #{dosesPerDispensingUnit}, #{dosesPerDay}," +
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

    @Results(value = {
            @Result(property = "code", column = "code"),
            @Result(property = "primaryName", column = "primary_name"),
            @Result(property = "dispensingUnit", column = "dispensing_unit"),
            @Result(property = "formCode", column = "form", javaType = java.lang.String.class, one = @One(select = "getProductFormCodeFor")),
            @Result(property = "strength", column = "strength"),
            @Result(property = "dosageUnitCode", column = "dosage_unit", javaType = java.lang.String.class, one = @One(select = "getDosageUnitCodeFor")),
            @Result(property = "productForm.id", column = "form"),
            @Result(property = "productForm.code", column = "form_code"),
            @Result(property = "productForm.displayOrder", column = "form_display_order"),
            @Result(property = "productDosageUnit.id", column = "dosage_unit"),
            @Result(property = "productDosageUnit.code", column = "dosage_unit_code"),
            @Result(property = "productDosageUnit.displayOrder", column = "dosage_unit_display_order")
    })
    @Select("select p.code as code, p.primary_name as primary_name, " +
            "p.dispensing_unit as dispensing_unit, p.dosage_unit as dosage_unit, p.form as form, p.strength as strength, " +
            "pf.code as form_code , pf.display_order as form_display_order, " +
            "du.code as dosage_unit_code, du.display_order as dosage_unit_display_order " +
             "from product p, facility_approved_product fap, program_product pp, facility f , product_form pf , dosage_unit du " +
            "where pp.program_code = #{programCode} " +
            "and f.id = #{facilityId}" +
            "and fap.facility_type_id= f.type " +
            "and fap.product_code = p.code " +
            "and fap.product_code = pp.product_code " +
            "and pp.product_code = p.code " +
            "and pf.id = p.form " +
            "and du.id = p.dosage_unit " +
            "and p.full_supply = 'TRUE' " +
            "and p.active = true " +
            "and pp.active = true " +
            "ORDER BY p.display_order NULLS LAST, p.code")
    List<Product> getFullSupplyProductsByFacilityAndProgram(@Param("facilityId") int facilityId, @Param("programCode") String programCode);

    @Select("SELECT code FROM product_form where id = #{id}")
    public String getProductFormCodeFor(Integer id);

    @Select("SELECT code FROM dosage_unit where id = #{id}")
    public String getDosageUnitCodeFor(Integer id);

    @Delete("delete from product")
    void deleteAll();

}
