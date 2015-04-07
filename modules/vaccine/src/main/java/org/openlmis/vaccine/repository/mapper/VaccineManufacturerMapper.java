/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.smt.Manufacturer;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineManufacturerMapper {


    @Update("UPDATE manufacturers " +
            " SET  " +
            " name=#{name}, website=#{website}, contactperson=#{contactPerson}, primaryphone=#{primaryPhone},  " +
            " email=#{email}, description=#{description}, specialization=#{specialization}, geographiccoverage=#{geographicCoverage},  " +
            " registrationdate=#{registrationDate}, modifiedby=#{modifiedBy}, modifieddate = COALESCE(#{modifiedDate}, CURRENT_TIMESTAMP) " +
            " WHERE id = #{id}")
    public void updateVaccineManufacturer( Manufacturer vaccineManufacturer);

    @Select("select * from manufacturers")
    List< Manufacturer> getVaccineManufacturers();

    @Select("select * from manufacturers where id = #{id}")
    Manufacturer getVaccineManufacturer(Long id);

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
    void insertVaccineManufacturer(Manufacturer vaccineManufacturer);

    @Delete("delete from manufacturers where id = #{id}")
    void deleteVaccineManufacturer(Long id);
}


