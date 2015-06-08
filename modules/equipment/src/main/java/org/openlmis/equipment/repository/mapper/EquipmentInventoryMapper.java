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
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentInventoryMapper {

  @Select("SELECT * from equipment_inventories where facilityId = #{facilityId} and programId = #{programId}")
  @Results({
      @Result(
          property = "equipment", column = "equipmentId", javaType = Equipment.class,
          one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentMapper.getById"))
  })
  List<EquipmentInventory> getInventoryByFacilityAndProgram(@Param("facilityId") Long facilityId, @Param("programId")Long programId);

  @Select("SELECT ei.*" +
      " FROM equipment_inventories ei" +
      " JOIN equipments e ON ei.equipmentId = e.id" +
      " JOIN equipment_types et ON e.equipmentTypeId = et.id" +
      " WHERE ei.programId = #{programId}" +
      " AND et.id = #{equipmentTypeId}" +
      " AND ei.facilityId = ANY (#{facilityIds}::INT[])")
  @Results({
      @Result(property = "equipmentId", column = "equipmentId"),
      @Result(
          property = "equipment", column = "equipmentId", javaType = Equipment.class,
          one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentMapper.getById")),
      @Result(property = "facilityId", column = "facilityId"),
      @Result(
          property = "facility", column = "facilityId", javaType = Facility.class,
          one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
  })
  List<EquipmentInventory> getInventory(@Param("programId")Long programId, @Param("equipmentTypeId")Long equipmentTypeId, @Param("facilityIds")String facilityIds, RowBounds rowBounds);

  @Select("SELECT COUNT(ei.id)" +
      " FROM equipment_inventories ei" +
      " JOIN equipments e ON ei.equipmentId = e.id" +
      " JOIN equipment_types et ON e.equipmentTypeId = et.id" +
      " WHERE ei.programId = #{programId}" +
      " AND et.id = #{equipmentTypeId}" +
      " AND ei.facilityId = ANY (#{facilityIds}::INT[])")
  Integer getInventoryCount(@Param("programId")Long programId, @Param("equipmentTypeId")Long equipmentTypeId, @Param("facilityIds")String facilityIds);

  @Select("SELECT * from equipment_inventories where id = #{id}")
  @Results({
      @Result(property = "equipmentId", column = "equipmentId"),
      @Result(
          property = "equipment", column = "equipmentId", javaType = Equipment.class,
          one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentMapper.getById")),
      @Result(property = "facilityId", column = "facilityId"),
      @Result(
          property = "facility", column = "facilityId", javaType = Facility.class,
          one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
  })
  EquipmentInventory getInventoryById(@Param("id") Long id);

  @Insert("INSERT into equipment_inventories " +
      " ( facilityId, equipmentId, programId, operationalStatusId, notFunctionalStatusId, serialNumber" +
      ", yearOfInstallation, purchasePrice, sourceOfFund, replacementRecommended, reasonForReplacement" +
      ", nameOfAssessor, dateLastAssessed, isActive, dateDecommissioned" +
      ", primaryDonorId, createdBy, createdDate, modifiedBy, modifiedDate) " +
      "values " +
      " ( #{facilityId}, #{equipmentId}, #{programId}, #{operationalStatusId}, #{notFunctionalStatusId}, #{serialNumber}" +
      ", #{yearOfInstallation}, #{purchasePrice}, #{sourceOfFund}, #{replacementRecommended}, #{reasonForReplacement}" +
      ", #{nameOfAssessor}, #{dateLastAssessed}, #{isActive}, #{dateDecommissioned}" +
      ", #{primaryDonorId}, #{createdBy}, NOW(), #{modifiedBy}, NOW())")
  @Options(useGeneratedKeys = true)
  void insert(EquipmentInventory inventory);

  @Update("UPDATE equipment_inventories " +
      "SET " +
      " facilityId = #{facilityId}, equipmentId = #{equipmentId}, programId = #{programId}, " +
      " operationalStatusId = #{operationalStatusId}, notFunctionalStatusId = #{notFunctionalStatusId}, " +
      " serialNumber = #{serialNumber}, yearOfInstallation = #{yearOfInstallation}, purchasePrice = #{purchasePrice}, " +
      " sourceOfFund = #{sourceOfFund}, replacementRecommended = #{replacementRecommended}, " +
      " reasonForReplacement = #{reasonForReplacement}, nameOfAssessor = #{nameOfAssessor}, " +
      " dateLastAssessed = #{dateLastAssessed} " +
      " , isActive = #{isActive}, dateDecommissioned = #{dateDecommissioned}, primaryDonorId = #{primaryDonorId} " +
      " , modifiedBy = #{modifiedBy}, modifiedDate = NOW() " +
      " WHERE id = #{id}")
  void update(EquipmentInventory inventory);

  @Update("UPDATE equipment_inventories" +
      " SET operationalStatusId = #{operationalStatusId}" +
      " , modifiedBy = #{modifiedBy}, modifiedDate = NOW() " +
      " WHERE id = #{id}")
  void updateStatus(EquipmentInventory inventory);

}
