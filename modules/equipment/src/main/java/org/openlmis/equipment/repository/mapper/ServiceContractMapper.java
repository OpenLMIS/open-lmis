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
import org.openlmis.equipment.domain.ServiceContract;
import org.openlmis.equipment.dto.ContractDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
The ServiceContractMapper contains mappers for the following tables:
    * Equipment_Service_Contracts
    * Equipment_Service_Contract_Facilities
    * Equipment_Service_Contract_Equipments
 */

@Repository
public interface ServiceContractMapper {

  @Select("select * from equipment_service_contracts where id = #{id}")
  @Results(value = {
      @Result(property = "facilities", column = "id", javaType = List.class, many = @Many(select = "getFacilityOptions")),
      @Result(property = "equipments", column = "id", javaType = List.class, many = @Many(select = "getEquipments")),
      @Result(property = "serviceTypes", column = "id", javaType = List.class, many = @Many(select = "getServiceTypes"))
  })
  ServiceContract getById(Long id);

  @Select("select id,code || ' - ' || name as name, true as isActive from facilities where id in (select facilityId from equipment_service_contract_facilities where contractId = #{id}) " +
      " UNION " +
      " select f.id, f.code || ' - ' || f.name as name, false as isActive from facilities f where  f.id not in (select facilityId from equipment_service_contract_facilities where contractId = #{id}) " +
      " ORDER BY name ")
  List<ContractDetail> getFacilityOptions(Long id);

  @Select("select id, name, true as isActive from equipment_service_types where id in (select serviceTypeId from equipment_contract_service_types where contractId = #{id}) " +
      " UNION " +
      " select id, name, false as isActive from equipment_service_types where id not in (select serviceTypeId from equipment_contract_service_types where contractId = #{id}) " +
      " ORDER BY name")
  List<ContractDetail> getServiceTypes(Long id);

  @Select("select id, name, true as isActive from equipments where id in (select equipmentTypeId from equipment_service_contract_equipment_types where contractId = #{id}) " +
      " UNION " +
      " select id, name, false as isActive from equipments where id not in (select equipmentTypeId from equipment_service_contract_equipment_types where contractId = #{id}) " +
      " order by name")
  List<ContractDetail> getEquipments(@Param("id") Long id);

  @Select("select * from equipment_service_contracts")
  List<ServiceContract> getAll();

  @Select("select * from equipment_service_contracts where id in (select contractId from equipment_service_contract_facilities where facilityId = #{facilityId} )")
  List<ServiceContract> getAllForFacility(@Param("facilityId") Long facilityId);

  @Select("select * from equipment_service_contracts where vendorId = #{vendorId}")
  List<ServiceContract> getAllForVendor(@Param("vendorId") Long vendorId);

  @Select("SELECT * FROM equipment_service_contracts WHERE id IN (SELECT contractId FROM equipment_service_contract_equipment_types WHERE equipmentTypeId = #{equipmentId})")
  List<ServiceContract> getAllForEquipment(@Param("equipmentId") Long equipmentId);

  @Insert("insert into equipment_service_contracts (vendorId, identifier, startDate, endDate,description, terms, coverage, contractDate, createdBy, createdDate, modifiedBy, modifiedDate) " +
      " values " +
      " (#{vendorId}, #{identifier}, #{startDate}, #{endDate}, #{description}, #{terms}, #{coverage}, #{contractDate}, #{createdBy},COALESCE(#{createdDate}, NOW()), #{modifiedBy}, NOW() )")
  @Options(useGeneratedKeys = true)
  void insert(ServiceContract value);

  @Update("UPDATE equipment_service_contracts SET " +
      "vendorId = #{vendorId}, identifier = #{identifier}, startDate = #{startDate}, endDate = #{endDate}, description = #{description}, terms = #{terms}, coverage = #{coverage}, contractDate = #{contractDate}, modifiedBy = #{modifiedBy}, modifiedDate = NOW()" +
      " WHERE id = #{id}")
  void update(ServiceContract value);

  @Delete("DELETE from equipment_service_contract_equipment_types where contractId = #{contractId}")
  void deleteEquipments(Long contractId);

  @Delete("DELETE from equipment_contract_service_types where contractId = #{contractId}")
  void deleteServiceTypes(Long contractId);

  @Delete("DELETE from equipment_service_contract_facilities where contractId = #{contractId}")
  void deleteFacilities(Long contractId);

  @Insert("INSERT INTO equipment_service_contract_equipment_types ( contractId, equipmentTypeId ) values (#{contractId}, #{equipmentId}) ")
  void insertEquipment(@Param("contractId") Long contractId, @Param("equipmentId") Long equipmentId);

  @Insert("INSERT INTO equipment_contract_service_types ( contractId, serviceTypeId ) values (#{contractId}, #{serviceTypeId}) ")
  void insertServiceTypes(@Param("contractId") Long contractId,@Param("serviceTypeId") Long serviceTypeId);

  @Insert("INSERT INTO equipment_service_contract_facilities ( contractId, facilityId ) values (#{contractId}, #{facilityId}) ")
  void insertFacilities(@Param("contractId") Long contractId,@Param("facilityId") Long facilityId);
}
