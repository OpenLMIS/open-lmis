package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.domain.ProductForm;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {

    @Insert("INSERT INTO products (" +
            "code, " +
            "alternateItemCode," +
            "manufacturer," + "manufacturerCode," + "manufacturerBarcode," +
            "mohBarcode," +
            "gtin," +
            "type," +
            "displayOrder," +
            "primaryName," + "fullName," + "genericName," + "alternateName," + "description," +
            "strength," +
            "formId," +
            "dosageUnitId, dispensingUnit, dosesPerDispensingUnit," +
            "packSize," + "alternatePackSize," +
            "storeRefrigerated," + "storeRoomTemperature," + "hazardous," + "flammable," + "controlledSubstance," + "lightSensitive," + "approvedByWho," +
            "contraceptiveCyp," +
            "packLength," + "packWidth," + "packHeight," + "packWeight," + "packsPerCarton," +
            "cartonLength," + "cartonWidth," + "cartonHeight," + "cartonsPerPallet," +
            "expectedShelfLife," +
            "specialStorageInstructions," + "specialTransportInstructions," +
            "active," + "fullSupply," + "tracer," + "roundToZero," + "archived," +
            "packRoundingThreshold, categoryId," +
            "modifiedBy, modifiedDate)" +
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
            "#{form.id}, " +
            "#{dosageUnit.id}," +
            " #{dispensingUnit}, #{dosesPerDispensingUnit}," +
            "#{packSize}," + "#{alternatePackSize}," +
            "#{storeRefrigerated}," + "#{storeRoomTemperature}," + "#{hazardous}," + "#{flammable}," + "#{controlledSubstance}," + "#{lightSensitive}," + "#{approvedByWHO}," +
            "#{contraceptiveCYP}," +
            "#{packLength}," + "#{packWidth}," + "#{packHeight}," + "#{packWeight}," + "#{packsPerCarton}," +
            "#{cartonLength}," + "#{cartonWidth}," + "#{cartonHeight}," + "#{cartonsPerPallet}," +
            "#{expectedShelfLife}," +
            "#{specialStorageInstructions}," + "#{specialTransportInstructions}," +
            "#{active}," + "#{fullSupply}," + "#{tracer}," + "#{roundToZero}," + "#{archived}," +
            "#{packRoundingThreshold}, #{category.id},  " +
            "#{modifiedBy}, #{modifiedDate})")
    @Options(useGeneratedKeys = true)
    Integer insert(Product product);

    @Select("SELECT id FROM dosage_Units where LOWER(code) = LOWER(#{code})")
    Integer getDosageUnitIdForCode(String code);

    @Select("SELECT id FROM product_forms where LOWER(code) = LOWER(#{code})")
    Integer getProductFormIdForCode(String code);

    // Used by ProgramProductMapper
    @Select("SELECT * FROM products WHERE id = #{id}")
    @Results(value = {
            @Result(property = "form", column = "formId", javaType = ProductForm.class, one = @One(select = "org.openlmis.core.repository.mapper.ProductFormMapper.getById")),
            @Result(property = "category", column = "categoryId", javaType = ProductCategory.class, one = @One(select = "org.openlmis.core.repository.mapper.ProductCategoryMapper.getProductCategoryById")),
            @Result(property = "dosageUnit", column = "dosageUnitId", javaType = ProductForm.class, one = @One(select = "org.openlmis.core.repository.mapper.DosageUnitMapper.getById"))
    })
    Product getById(Integer id);

    @Select("SELECT id from products where code = #{code}")
    Integer getIdByCode(String code);

    @Select("SELECT * FROM products")
    List<Product> getAll();
}
