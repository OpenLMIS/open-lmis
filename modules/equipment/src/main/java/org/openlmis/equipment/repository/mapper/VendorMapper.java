/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.equipment.domain.Vendor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorMapper {

  @Select("select * from equipment_service_vendors where id = #{id}")
  Vendor getById(Long id);

  @Select("select * from equipment_service_vendors order by name")
  List<Vendor> getAll();

  @Insert("insert into equipment_service_vendors (name, website, contactPerson, primaryPhone, email, description, specialization, geographicCoverage, registrationDate, createdBy, createdDate, modifiedBy, modifiedDate) " +
      " values " +
      " (#{name}, #{website}, #{contactPerson}, #{primaryPhone}, #{email}, #{description}, #{specialization}, #{geographicCoverage}, #{registrationDate},  #{createdBy},COALESCE(#{createdDate}, NOW()), #{modifiedBy}, NOW() )")
  @Options(useGeneratedKeys = true)
  void insert(Vendor vendor);

  @Update("UPDATE equipment_service_vendors SET " +
      "name = #{name}, website = #{website}, contactPerson = #{contactPerson}, primaryPhone = #{primaryPhone}, email = #{email}, description = #{description}, specialization = #{specialization}, geographicCoverage = #{geographicCoverage}, registrationDate = #{registrationDate}, modifiedBy = #{modifiedBy}, modifiedDate = NOW()" +
      " WHERE id = #{id}")
  void update(Vendor vendor);

  @Delete("DELETE FROM equipment_service_vendors WHERE ID = #{id}")
  void remove(Long id);
}
