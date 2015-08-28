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
import org.apache.ibatis.session.RowBounds;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentEnergyType;
import org.openlmis.equipment.domain.EquipmentType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentMapper {

  @Select("SELECT equipments.*" +
      "   , COUNT(equipment_inventories.id) AS inventorycount" +
      " FROM equipments" +
      "   LEFT JOIN equipment_inventories ON equipment_inventories.equipmentid = equipments.id" +
      " GROUP BY equipments.id")
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

    @Select("SELECT equipments.*" +
        "   , COUNT(equipment_inventories.id) AS inventorycount" +
        " FROM equipments" +
        "   LEFT JOIN equipment_inventories ON equipment_inventories.equipmentid = equipments.id" +
        " WHERE equipmentTypeId = #{equipmentTypeId}" +
        " GROUP BY equipments.id" +
        " ORDER BY id DESC")
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
    List<Equipment> getByType(@Param("equipmentTypeId") Long equipmentTypeId, RowBounds rowBounds);

  @Select("SELECT equipments.*" +
      "   , COUNT(equipment_inventories.id) AS inventorycount" +
      " FROM equipments" +
      "   LEFT JOIN equipment_inventories ON equipment_inventories.equipmentid = equipments.id" +
      " WHERE equipmentTypeId = #{equipmentTypeId}" +
      " GROUP BY equipments.id" +
      " ORDER BY name")
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

  @Select("SELECT COUNT(id) FROM equipments WHERE equipmentTypeId = #{equipmentTypeId} ")
  Integer getCountByType(@Param("equipmentTypeId") Long equipmentTypeId);

  @Select("SELECT equipments.*" +
      "   , COUNT(equipment_inventories.id) AS inventorycount" +
      " FROM equipments" +
      "   LEFT JOIN equipment_inventories ON equipment_inventories.equipmentid = equipments.id" +
      " WHERE equipments.id = #{id}" +
      " GROUP BY equipments.id")
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
