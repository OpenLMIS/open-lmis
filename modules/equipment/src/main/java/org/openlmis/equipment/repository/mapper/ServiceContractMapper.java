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

import org.apache.ibatis.annotations.*;
import org.openlmis.equipment.domain.ServiceContract;
import org.openlmis.equipment.domain.ServiceType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceContractMapper {

  @Select("select * from equipment_service_contracts where id = #{id}")
  ServiceContract getById(Long id);

  @Select("select * from equipment_service_contracts")
  List<ServiceContract> getAll();

  @Select("select * from equipment_service_contracts where id in (select contractId from equipment_service_contract_facilities where facilityId = #{facilityId} )")
  List<ServiceContract> getAllForFacility(@Param("facilityId") Long facilityId);

  @Select("select * from equipment_service_contracts where vendorId = #{vendorId}")
  List<ServiceContract> getAllForVendor(@Param("vendorId") Long vendorId);

  @Insert("insert into equipment_service_contracts (vendorId, identifier, startDate, endDate,description, terms, coverage, contractDate, createdBy, createdDate, modifiedBy, modifiedDate) " +
      " values " +
      " (#{vendorId}, #{identifier}, #{startDate}, #{endDate}, #{description}, #{terms}, #{coverage}, #{contractDate}, #{createdBy},COALESCE(#{createdDate}, NOW()), #{modifiedBy}, NOW() )")
  @Options(useGeneratedKeys = true)
  void insert(ServiceContract value);

  @Update("UPDATE equipment_service_contracts SET " +
      "vendorId = #{vendorId}, identifier = #{identifier}, startDate = #{startDate}, endDate = #{endDate}, description = #{description}, terms = #{terms}, coverage = #{coverage}, contractDate = #{contractDate}, modifiedBy = #{modifiedBy}, modifiedDate = NOW()" +
      " WHERE id = #{id}")
  void update(ServiceContract value);
}
