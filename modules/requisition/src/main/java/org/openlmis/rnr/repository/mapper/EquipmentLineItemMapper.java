/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.rnr.domain.EquipmentLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentLineItemMapper {

  @Insert("INSERT INTO equipment_status_line_items (rnrId, code, equipmentName, equipmentCategory, equipmentModel, equipmentSerial, equipmentInventoryId, operationalStatusId, testCount, totalCount, daysOutOfUse, remarks, createdBy, createdDate, modifiedBy, modifiedDate) values (#{rnrId}, #{code}, #{equipmentName}, #{equipmentCategory}, #{equipmentModel}, #{equipmentSerial}, #{equipmentInventoryId}, #{operationalStatusId}, #{testCount}, #{totalCount}, #{daysOutOfUse}, #{remarks}, #{createdBy}, #{createdDate}, #{modifiedBy}, #{modifiedDate})")
  public void insert(EquipmentLineItem item);

  @Update("UPDATE equipment_status_line_items " +
      " SET " +
      "code = #{code}, equipmentName = #{equipmentName}, equipmentCategory = #{equipmentCategory}, equipmentModel = #{equipmentModel}, equipmentSerial = #{equipmentSerial}, equipmentInventoryId = #{equipmentInventoryId} " +
      " , operationalStatusId = #{operationalStatusId}, testCount = #{testCount}, totalCount = #{totalCount}, daysOutOfUse = #{daysOutOfUse}, remarks = #{remarks}, modifiedBy = #{modifiedBy}, modifiedDate = NOW()" +
      " where id = #{id}")
  public void update(EquipmentLineItem item);

  @Select("SELECT esli.id, sq.id as programEquipmentId, esli.* from equipment_status_line_items esli " +
      "JOIN facility_program_equipments inv ON esli.equipmentInventoryId = inv.id " +
      " LEFT JOIN ( select * from program_equipments e where e.programId in " +
      " (select max(programId) from requisitions where id = #{rnrId} ) ) sq" +
      " ON sq.equipmentId = inv.equipmentId" +
      " where " +
      " rnrId = #{rnrId} ")
  @Results(
      value = {
          @Result(property = "id", column = "id"),
          @Result(property = "relatedProducts", javaType = List.class, column = "id", many = @Many(select = "org.openlmis.rnr.repository.mapper.EquipmentLineItemMapper.getRelatedRnrLineItems"))
  })

  public List<EquipmentLineItem> getEquipmentLineItemsByRnrId(@Param("rnrId") Long rnrId);


  @Select("select rli.id, p.primaryName from " +
                " requisitions r " +
      "         JOIN requisition_line_items rli on r.id = rli.rnrId " +
      "         JOIN products p on p.code::text = rli.productCode::text " +
      "         JOIN equipment_status_line_items esli on esli.rnrId = r.id " +
      "         JOIN program_equipments pe on pe.programId = r.programId " +
      "         JOIN program_equipment_products ep on pe.id = ep.programEquipmentId " +
      "               and p.id = ep.productId " +
      " WHERE " +
      "       esli.id = #{id}")
  public List<Product> getRelatedRnrLineItems(@Param("id") Long id);
}
