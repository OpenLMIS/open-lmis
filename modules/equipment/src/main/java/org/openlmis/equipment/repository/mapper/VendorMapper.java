/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.domain.Vendor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorMapper {

  @Select("select * from equipment_service_vendors where id = #{id}")
  Vendor getById(Long id);

  @Select("select * from equipment_service_vendors")
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
}
