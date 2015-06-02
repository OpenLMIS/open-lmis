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
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentEnergyType;
import org.openlmis.equipment.domain.EquipmentType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentMapper {

  @Select("SELECT * from equipments")
  @Results({
      @Result(
          property = "equipmentType", column = "equipmentTypeId", javaType = EquipmentType.class,
          one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentTypeMapper.getEquipmentTypeById")),
      @Result(property = "equipmentTypeId", column = "equipmentTypeId"),
          @Result(
                  property = "energyType", column = "energyTypeId", javaType = EquipmentEnergyType.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentEnergyTypeMapper.getById")),
          @Result(property = "energyTypeId", column = "energyTypeId")

  })
  List<Equipment> getAll();

  @Select("SELECT * from equipments where equipmentTypeId = #{equipmentTypeId} order by name")
  @Results({
      @Result(
          property = "equipmentType", column = "equipmentTypeId", javaType = EquipmentType.class,
          one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentTypeMapper.getEquipmentTypeById")),
      @Result(property = "equipmentTypeId", column = "equipmentTypeId"),
      @Result(
          property = "energyType", column = "energyTypeId", javaType = EquipmentEnergyType.class,
          one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentEnergyTypeMapper.getById")),
      @Result(property = "energyTypeId", column = "energyTypeId")

  })
  List<Equipment> getAllByType(@Param("equipmentTypeId") Long equipmentTypeId);

  @Select("SELECT * from equipments where id = #{id}")
  @Results({
          @Result(
                  property = "equipmentType", column = "equipmentTypeId", javaType = EquipmentType.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentTypeMapper.getEquipmentTypeById")),
          @Result(property = "equipmentTypeId", column = "equipmentTypeId"),
          @Result(
                  property = "energyType", column = "energyTypeId", javaType = EquipmentEnergyType.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentEnergyTypeMapper.getById")),
          @Result(property = "energyTypeId", column = "energyTypeId")

  })
  Equipment getById(@Param("id") Long id);

  @Select("SELECT et.*" +
      " FROM equipment_types et" +
      " JOIN equipment_type_programs etp ON et.id = etp.equipmenttypeid" +
      " JOIN programs p ON etp.programid = p.id" +
      " WHERE p.id = #{programId}")
  List<EquipmentType> getTypesByProgram(@Param("programId") Long programId);

  @Insert("INSERT into equipments (name, equipmentTypeId, createdBy, createdDate, modifiedBy, modifiedDate, manufacturer, model, energyTypeId) " +
      "values " +
      "(#{name}, #{equipmentType.id}, #{createdBy}, NOW(), #{modifiedBy}, NOW(), #{manufacturer},#{model},#{energyTypeId})")
  @Options(useGeneratedKeys = true)
  void insert(Equipment equipment);

  @Update("UPDATE equipments " +
      "set " +
      " name = #{name}, equipmentTypeId = #{equipmentType.id}, modifiedBy = #{modifiedBy}, modifiedDate = NOW(), manufacturer = #{manufacturer}, model = #{model}, energyTypeId = #{energyTypeId} " +
      "WHERE id = #{id}")
  void update(Equipment equipment);

    @Delete("DELETE FROM equipments WHERE id = #{Id}")
    void remove(Long Id);
}
