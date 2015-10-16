/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.domain.ProductGroup;
import org.springframework.stereotype.Repository;

/**
 * ProductMapper maps the Product entity to corresponding representation in database.
 */
@Repository
public interface ProductMapper {

  @Insert("INSERT INTO products (" +
    "code, " +
    "alternateItemCode," +
    "manufacturer," + "manufacturerCode," + "manufacturerBarCode," +
    "mohBarCode," +
    "gtin," +
    "type," +
    "primaryName," + "fullName," + "genericName," + "alternateName," + "description," +
    "strength," +
    "formId," +
    "dosageUnitId, dispensingUnit, dosesPerDispensingUnit," +
    "packSize," + "alternatePackSize," +
    "storeRefrigerated," + "storeRoomTemperature," + "hazardous," + "flammable," + "controlledSubstance," + "lightSensitive," + "approvedByWHO," +
    "contraceptiveCYP," +
    "packLength," + "packWidth," + "packHeight," + "packWeight," + "packsPerCarton," +
    "cartonLength," + "cartonWidth," + "cartonHeight," + "cartonsPerPallet," +
    "expectedShelfLife," +
    "specialStorageInstructions," + "specialTransportInstructions," +
    "active," + "fullSupply," + "tracer," + "roundToZero," + "archived," +
    "packRoundingThreshold, productGroupId," +
    "createdBy, modifiedBy, modifiedDate)" +
    "VALUES(" +
    "#{code}," +
    "#{alternateItemCode}," +
    "#{manufacturer}," + "#{manufacturerCode}," + "#{manufacturerBarCode}," +
    "#{mohBarCode}," +
    "#{gtin}," +
    "#{type}," +
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
    "#{packRoundingThreshold},  #{productGroup.id}," +
    "#{createdBy}, #{modifiedBy}, COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP))")
  @Options(useGeneratedKeys = true)
  Long insert(Product product);

  @Delete("DELETE FROM products WHERE code=#{code}")
  public void deleteByCode(String code);

  @Select("SELECT * FROM dosage_Units WHERE LOWER(code) = LOWER(#{code})")
  DosageUnit getDosageUnitByCode(String code);

  @Select("SELECT id FROM products WHERE LOWER(code) = LOWER(#{code})")
  Long getIdByCode(String code);

  @Select("SELECT * FROM products WHERE LOWER(code)=LOWER(#{code})")
  @Results({
    @Result(
      property = "form", column = "formId", javaType = ProductForm.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductFormMapper.getById")),
    @Result(
      property = "dosageUnit", column = "dosageUnitId", javaType = DosageUnit.class,
      one = @One(select = "org.openlmis.core.repository.mapper.DosageUnitMapper.getById"))})
  Product getByCode(String code);

  @Update({"UPDATE products SET code = #{code}, alternateItemCode = #{alternateItemCode}, ", "manufacturer = #{manufacturer},",
    "manufacturerCode = #{manufacturerCode}, manufacturerBarcode = #{manufacturerBarCode}, mohBarcode = #{mohBarCode}, ",
    "gtin = #{gtin}, type = #{type}, primaryName = #{primaryName}, fullName = #{fullName}, genericName = #{genericName},",
    "alternateName=#{alternateName},description=#{description}, ", "strength=#{strength}, formId=#{form.id}, ", "dosageUnitId=#{dosageUnit.id}, dispensingUnit=#{dispensingUnit}, ",
    "dosesPerDispensingUnit=#{dosesPerDispensingUnit}, ", "packSize=#{packSize},alternatePackSize=#{alternatePackSize}, ", "storeRefrigerated=#{storeRefrigerated},storeRoomTemperature=#{storeRoomTemperature}, ", "hazardous=#{hazardous},",
    "flammable=#{flammable},controlledSubstance=#{controlledSubstance},lightSensitive=#{lightSensitive},approvedByWHO=#{approvedByWHO}, ", "contraceptiveCYP=#{contraceptiveCYP},", "packLength=#{packLength},packWidth=#{packWidth},packHeight=#{packHeight},",
    "packWeight=#{packWeight},packsPerCarton=#{packsPerCarton},", "cartonLength=#{cartonLength},cartonWidth=#{cartonWidth},cartonHeight=#{cartonHeight},cartonsPerPallet=#{cartonsPerPallet},", "expectedShelfLife=#{expectedShelfLife},",
    "specialStorageInstructions=#{specialStorageInstructions},specialTransportInstructions=#{specialTransportInstructions},", "active=#{active},fullSupply=#{fullSupply},tracer=#{tracer},roundToZero=#{roundToZero},archived=#{archived},",
    "packRoundingThreshold=#{packRoundingThreshold}, productGroupId = #{productGroup.id},", "modifiedBy=#{modifiedBy}, modifiedDate=COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP) WHERE id=#{id}"})
  void update(Product product);

  @Select("SELECT * FROM products WHERE id=#{id}")
  @Results({
    @Result(property = "productGroup", column = "productGroupId", javaType = ProductGroup.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductGroupMapper.getById")),
    @Result(
      property = "dosageUnit", column = "dosageUnitId", javaType = DosageUnit.class,
      one = @One(select = "org.openlmis.core.repository.mapper.DosageUnitMapper.getById")),
    @Result(
      property = "form", column = "formId", javaType = DosageUnit.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductFormMapper.getById"))})
  Product getById(Long id);

  @Select("SELECT active FROM products WHERE LOWER(code) = LOWER(#{code})")
  boolean isActive(String code);

  @Select({"SELECT id, fullSupply, code, primaryName, strength, dosageUnitId, dispensingUnit, packSize, active",
    "FROM products WHERE id = #{id}"})
  @Results({
    @Result(property = "dosageUnit", column = "dosageUnitId", javaType = DosageUnit.class,
      one = @One(select = "org.openlmis.core.repository.mapper.DosageUnitMapper.getById"))})
  Product getLWProduct(Long id);

  @Select({"SELECT COUNT(*) FROM products WHERE (LOWER(code) LIKE '%' || LOWER(#{searchParam}) || '%')",
    "OR (LOWER(primaryName) LIKE '%' || LOWER(#{searchParam}) || '%')"})
  Integer getTotalSearchResultCount(String searchParam);
}
