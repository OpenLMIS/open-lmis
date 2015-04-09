/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.smt.Manufacturer;
import org.openlmis.vaccine.domain.smt.ManufacturerProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManufacturerMapper {

    @Select("Select * from manufacturers")
    List<Manufacturer> getAll();

    @Update("UPDATE manufacturers " +
            " SET  " +
            " name=#{name}, website=#{website}, contactperson=#{contactPerson}, primaryphone=#{primaryPhone},  " +
            " email=#{email}, description=#{description}, specialization=#{specialization}, geographiccoverage=#{geographicCoverage},  " +
            " registrationdate=#{registrationDate}, modifiedby=#{modifiedBy}, modifieddate = COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP) " +
            " WHERE id = #{id}")
    public void update( Manufacturer vaccineManufacturer);

    @Select("select * from manufacturers where id = #{id}")
    Manufacturer get(Long id);

    @Insert(" INSERT INTO manufacturers ( " +
            "            name, website, contactperson, primaryphone, email, description,  " +
            "            specialization, geographiccoverage, registrationdate, createdby, createddate) " +
            "    VALUES ( " +
            "#{name}, " +
            " #{website}, " +
            " #{contactPerson}, " +
            " #{primaryPhone}, " +
            " #{email}, " +
            " #{description}, " +
            " #{ specialization}, " +
            " #{geographicCoverage}, " +
            " #{registrationDate}, " +
            " #{createdBy}, " +
            " COALESCE(#{createdDate}, NOW()) )")
    @Options(useGeneratedKeys = true)
    void insert(Manufacturer vaccineManufacturer);

    @Delete("delete from manufacturers where id = #{id}")
    void delete(Long id);

    @Select("SELECT product_mapping.*, products.fullname AS productName " +
            "  FROM product_mapping join  products on products.code = product_mapping.productCode where product_mapping.manufacturerId = #{manufacturerId}")
    public List<ManufacturerProduct> getProductMapping(Long manufacturerId);

    @Select("select * from product_mapping where id = #{productMappingId}")
    public ManufacturerProduct getProductMappingByMappingId(Long productMappingId);

    @Delete("Delete from product_mapping where  id = #{productMappingId}")
    public void deleteProductMapping(Long productMappingId);

    @Insert("INSERT INTO product_mapping(" +
            "            productcode, manufacturerid, gtin, elmis, rhi, ppmr, who, " +
            "            other1, other2, other3, other4, other5, createdby, createddate)" +
            "    VALUES (" +
            "    #{productCode}," +
            "    #{manufacturerId}," +
            "    #{gtin}," +
            "    #{elmis}," +
            "    #{rhi}," +
            "    #{ppmr}," +
            "    #{who}," +
            "    #{other1}," +
            "    #{other2}," +
            "    #{other3}," +
            "    #{other4}," +
            "    #{other5}," +
            "    #{createdBy}," +
            "    COALESCE(#{createdDate}, NOW()) " +
            "    )")
    public void insertProductMapping(ManufacturerProduct manufacturerProduct);

    @Update(" UPDATE product_mapping" +
            "   SET  productcode=#{productCode}, manufacturerid=#{manufacturerId}, gtin=#{gtin}, elmis=#{elmis}, rhi=#{rhi}, " +
            "       ppmr=#{ppmr}, who=#{who}, other1=#{other1}, other2=#{other2}, other3=#{other3}, other4=#{other4}, other5=#{other5}, " +
            "       modifiedby=#{modifiedBy}, modifieddate = COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP)" +
            " WHERE id = #{id}")
    public void updateProductMapping(ManufacturerProduct manufacturerProduct);

}
