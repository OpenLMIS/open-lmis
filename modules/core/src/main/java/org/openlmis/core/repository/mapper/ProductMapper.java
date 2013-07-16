/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
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
  Integer insert(Product product);

  @Select("SELECT id FROM dosage_Units WHERE LOWER(code) = LOWER(#{code})")
  Long getDosageUnitIdForCode(String code);

  @Select("SELECT id FROM product_forms WHERE LOWER(code) = LOWER(#{code})")
  Long getProductFormIdForCode(String code);

  @Select("SELECT id FROM products WHERE LOWER(code) = LOWER(#{code})")
  Long getIdByCode(String code);

  @Select("SELECT * FROM products WHERE LOWER(code)=LOWER(#{code})")
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

    // mahmed 07.11.2013 full product list
  @Select(value = "SELECT\n" +
          "products.id,\n" +
          "products.code,\n" +
          "products.fullname,\n" +
          "product_categories.name AS type,\n" +
          "products.strength,\n" +
          "products.dispensingunit AS dispensingUnit,\n" +
          "product_forms.code,\n" +
          "dosage_units.code,\n" +
          "product_forms.code,\n" +
          //"(CASE WHEN products.fullsupply = true THEN 'Yes' WHEN products.fullsupply = false THEN 'No' ELSE '' END) AS fullSupply,\n" +
          //"(CASE WHEN products.active = true THEN 'Yes' WHEN products.active = false THEN 'No' ELSE '' END) AS active,\n" +
          "products.fullsupply AS fullSupply,\n" +
          "products.active AS active,\n" +
          "products.displayorder AS displayOrder,\n" +
          "programs.id AS programId, \n" +
          "programs.name AS programName\n" +
          "FROM\n" +
          "products\n" +
          "INNER JOIN product_forms ON product_forms.id = products.formid\n" +
          "INNER JOIN dosage_units ON dosage_units.id = products.dosageunitid\n" +
          "INNER JOIN product_categories ON product_categories.id = products.categoryid\n" +
          "INNER JOIN program_products ON products.id = program_products.productid\n" +
          "INNER JOIN programs ON programs.id = program_products.programid")
  List<Product> getList();

    // mahmed - 07.11.2013 - delete supply line
    @Update("UPDATE products SET  active=false where id = #{productId}")
    int deleteById(Long productId);

    // mahmed - 07.11.2013 - delete supply line
    @Update("UPDATE products SET  active=true where id = #{productId}")
    int restoreById(Long productId);
 }
