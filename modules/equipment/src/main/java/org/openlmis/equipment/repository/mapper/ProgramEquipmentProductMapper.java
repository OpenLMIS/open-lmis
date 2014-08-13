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
import org.openlmis.equipment.domain.ProgramEquipmentProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramEquipmentProductMapper {

  @Select("select pep.*, p.fullName productName, p.primaryName " +
      "      from program_equipment_products pep " +
      "      join products p on p.id = pep.productId " +
      "      where programEquipmentId = #{programEquipmentId} order by productName")
  @Results(value = {
      @Result(property = "product.fullName",column = "productName"),
      @Result(property="product.primaryName",column = "primaryName")
  })
  List<ProgramEquipmentProduct> getByProgramEquipmentId(@Param(value="programEquipmentId") Long programEquipmentId);

  @Insert("INSERT INTO program_equipment_products(programEquipmentId, productId, createdBy, createdDate, modifiedBy, modifiedDate) " +
      "VALUES (#{programEquipment.id}, #{product.id}, #{createdBy}, #{createdDate}, #{modifiedBy}, #{modifiedDate}) ")
  @Options(useGeneratedKeys = true)
  void insert(ProgramEquipmentProduct programEquipmentProduct);

  @Update("UPDATE program_equipment_products " +
      "SET programEquipmentId = #{programEquipment.id}, productId = #{product.id}, createdBy = #{createdBy}, createdDate = #{createdDate}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} " +
      "WHERE id = #{id}")
  void update(ProgramEquipmentProduct programEquipmentProduct);

  @Delete("DELETE FROM program_equipment_products WHERE id = #{programEquipmentProductId}")
  void remove(@Param(value = "programEquipmentProductId") Long programEquipmentProductId);

  @Delete("DELETE FROM program_equipment_products WHERE programequipmentid = #{programEquipmentId}")
  void removeEquipmentProducts(@Param(value = "programEquipmentId") Long programEquipmentId);

}
