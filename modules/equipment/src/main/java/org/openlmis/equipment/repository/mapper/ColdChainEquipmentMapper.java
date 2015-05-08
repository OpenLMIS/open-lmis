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
import org.openlmis.equipment.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColdChainEquipmentMapper {

  @Select("SELECT * from equipment_cold_chain_equipments")
  @Results({
          @Result(property = "equipment", column = "equipmentId", javaType = Equipment.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentMapper.getById")),
          @Result(property = "designation", column = "designationId", javaType = ColdChainEquipmentDesignation.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.ColdChainEquipmentDesignationMapper.getById")),
          @Result(property = "energyType", column = "energyTypeId", javaType = ColdChainEquipmentEnergyType.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.ColdChainEquipmentEnergyTypeMapper.getById")),
          @Result(property = "pqsStatus", column = "pqsStatusId", javaType = ColdChainEquipmentPqsStatus.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.ColdChainEquipmentPqsStatusMapper.getById")),
          @Result(property = "donor", column = "donorId", javaType = Donor.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.DonorMapper.getById"))
  })
  List<ColdChainEquipment> getAll();

    @Select("SELECT * from equipment_cold_chain_equipments where id = #{id}")
    @Results({
            @Result(property = "equipment", column = "equipmentId", javaType = Equipment.class,
                    one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentMapper.getById")),
            @Result(property = "designation", column = "designationId", javaType = ColdChainEquipmentDesignation.class,
                    one = @One(select = "org.openlmis.equipment.repository.mapper.ColdChainEquipmentDesignationMapper.getById")),
            @Result(property = "energyType", column = "energyTypeId", javaType = ColdChainEquipmentEnergyType.class,
                    one = @One(select = "org.openlmis.equipment.repository.mapper.ColdChainEquipmentEnergyTypeMapper.getById")),
            @Result(property = "pqsStatus", column = "pqsStatusId", javaType = ColdChainEquipmentPqsStatus.class,
                    one = @One(select = "org.openlmis.equipment.repository.mapper.ColdChainEquipmentPqsStatusMapper.getById")),
            @Result(property = "donor", column = "donorId", javaType = Donor.class,
                    one = @One(select = "org.openlmis.equipment.repository.mapper.DonorMapper.getById"))
    })
  ColdChainEquipment getById(@Param("id") Long id);

  @Insert("INSERT into equipment_cold_chain_equipments " +
      "(equipmentId, designationId, brand, model, cceCode, pqsCode, refrigeratorCapacity,freezerCapacity" +
      ", refrigerant, temperatureZone, maxTemperature, minTemperature, holdoverTime, energyConsumption, energyTypeId" +
      ", dimension, price, pqsStatusId, donorId, createdBy, createdDate, modifiedBy, modifiedDate)" +
      "values " +
      " ( #{equipmentId}, #{designationId}, #{brand}, #{model}, #{cceCode}, #{pqsCode}, #{refrigeratorCapacity},#{freezerCapacity}" +
      ", #{refrigerant}, #{temperatureZone}, #{maxTemperature}, #{minTemperature}, #{holdoverTime},#{energyConsumption},#{energyTypeId}" +
      ", #{dimension}, #{price}, #{pqsStatusId}, #{donorId}" +
      ", #{createdBy}, NOW(), #{modifiedBy}, NOW())")
  @Options(useGeneratedKeys = true)
  void insert(ColdChainEquipment coldChainEquipment);

  @Update("UPDATE equipment_cold_chain_equipments " +
      "SET " +
      " designationId = #{designationId}, brand = #{brand}, model = #{model}, cceCode = #{cceCode}, pqsCode = #{pqsCode}, refrigeratorCapacity = #{refrigeratorCapacity}, freezerCapacity = #{freezerCapacity}" +
      " , refrigerant = #{refrigerant}, temperatureZone = #{temperatureZone}, maxTemperature = #{maxTemperature}, minTemperature = #{minTemperature} , holdoverTime = #{holdoverTime} " +
      " , energyConsumption = #{energyConsumption},energyTypeId = #{energyTypeId}, dimension = #{dimension}, price = #{price}, pqsStatusId = #{pqsStatusId},donorId=#{donorId} " +
      " , modifiedBy = #{modifiedBy}, modifiedDate = NOW() " +
      " WHERE id = #{id}")
  void update(ColdChainEquipment coldChainEquipment);

}
