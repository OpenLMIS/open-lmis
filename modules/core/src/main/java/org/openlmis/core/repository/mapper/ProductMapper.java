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
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.domain.ProductGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {

  @Insert("INSERT INTO products (" +
    "code, " +
    "alternateItemCode," +
    "manufacturer," + "manufacturerCode," + "manufacturerBarCode," +
    "mohBarCode," +
    "gtin," +
    "type," +
    "displayOrder," +
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
    "packRoundingThreshold, categoryId, productGroupId," +
    "createdBy, modifiedBy, modifiedDate)" +
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
    "#{packRoundingThreshold}, #{category.id},  #{productGroup.id}," +
    "#{createdBy}, #{modifiedBy}, #{modifiedDate})")
  @Options(useGeneratedKeys = true)
  Long insert(Product product);

  @Select("SELECT id FROM dosage_Units WHERE LOWER(code) = LOWER(#{code})")
  Long getDosageUnitIdForCode(String code);

  @Select("SELECT id FROM product_forms WHERE LOWER(code) = LOWER(#{code})")
  Long getProductFormIdForCode(String code);

  @Select("SELECT id FROM products WHERE LOWER(code) = LOWER(#{code})")
  Long getIdByCode(String code);

  @Select("SELECT * FROM products WHERE LOWER(code)=LOWER(#{code})")
  @Results({
    @Result(property = "category", column = "categoryId", javaType = ProductCategory.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductCategoryMapper.getProductCategoryById"))
  })
  Product getByCode(String code);

  @Update({"UPDATE products SET  alternateItemCode=#{alternateItemCode}, ",
    "manufacturer =#{manufacturer},manufacturerCode=#{manufacturerCode},manufacturerBarcode=#{manufacturerBarCode}, mohBarcode=#{mohBarCode}, ",
    "gtin=#{gtin},type=#{type}, ",
    "displayOrder=#{displayOrder}, ",
    "primaryName=#{primaryName},fullName=#{fullName}, genericName=#{genericName},alternateName=#{alternateName},description=#{description}, ",
    "strength=#{strength}, formId=#{form.id}, ",
    "dosageUnitId=#{dosageUnit.id}, dispensingUnit=#{dispensingUnit}, dosesPerDispensingUnit=#{dosesPerDispensingUnit}, ",
    "packSize=#{packSize},alternatePackSize=#{alternatePackSize}, ",
    "storeRefrigerated=#{storeRefrigerated},storeRoomTemperature=#{storeRoomTemperature}, ",
    "hazardous=#{hazardous},flammable=#{flammable},controlledSubstance=#{controlledSubstance},lightSensitive=#{lightSensitive},approvedByWHO=#{approvedByWHO}, ",
    "contraceptiveCYP=#{contraceptiveCYP},",
    "packLength=#{packLength},packWidth=#{packWidth},packHeight=#{packHeight},packWeight=#{packWeight},packsPerCarton=#{packsPerCarton},",
    "cartonLength=#{cartonLength},cartonWidth=#{cartonWidth},cartonHeight=#{cartonHeight},cartonsPerPallet=#{cartonsPerPallet},",
    "expectedShelfLife=#{expectedShelfLife},",
    "specialStorageInstructions=#{specialStorageInstructions},specialTransportInstructions=#{specialTransportInstructions},",
    "active=#{active},fullSupply=#{fullSupply},tracer=#{tracer},roundToZero=#{roundToZero},archived=#{archived},",
    "packRoundingThreshold=#{packRoundingThreshold}, categoryId=#{category.id}, productGroupId = #{productGroup.id},",
    "modifiedBy=#{modifiedBy}, modifiedDate=#{modifiedDate} WHERE id=#{id}"})
  void update(Product product);

  @Select("SELECT * FROM products WHERE id=#{id}")
  @Results({
    @Result(property = "productGroup", column = "productGroupId", javaType = ProductGroup.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductGroupMapper.getById"))
  })
  Product getById(Long id);


  @Select("SELECT active FROM products WHERE code = #{code}")
  boolean isActive(String code);
}
