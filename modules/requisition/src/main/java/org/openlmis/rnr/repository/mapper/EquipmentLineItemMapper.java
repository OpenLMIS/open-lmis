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

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.rnr.domain.EquipmentLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentLineItemMapper {

  @Insert("INSERT INTO equipment_status_line_items (code, equipmentName, equipmentCategory, equipmentModel, equipmentSerial, equipmentInventoryId, operationalStatusId, testCount, totalCount, daysOutOfUse, remarks, createdBy, createdDate, modifiedBy, modifiedDate) values (#{code}, #{equipmentName}, #{equipmentCategory}, #{equipmentModel}, #{equipmentSerial}, #{equipmentInventoryId}, #{operationalStatusId}, #{testCount}, #{totalCount}, #{daysOutOfUse}, #{remarks}, #{createdBy}, #{createdDate}, #{modifiedBy}, #{modifiedDate})")
  public void insert(EquipmentLineItem item);

  @Update("UPDATE equipment_status_line_items " +
      " SET " +
      "code = #{code}, equipmentName = #{equipmentName}, equipmentCategory = #{equipmentCategory}, equipmentModel = #{equipmentModel}, equipmentSerial = #{equipmentSerial}, equipmentInventoryId = #{equipmentInventoryId} " +
      " , operationalStatusId = #{operationalStatusId}, testCount = #{testCount}, totalCount = #{totalCount}, daysOutOfUse = #{daysOutOfUse}, remarks = #{remarks}, modifiedBy = #{modifiedBy}, modifiedDate = NOW()" +
      " where id = #{id}")
  public void update(EquipmentLineItem item);

  @Select("SELECT * from equipment_status_line_items where rnrId = #{rnrId}")
  public List<EquipmentLineItem> getEquipmentLineItemsByRnrId(@Param("rnrId") Long rnrId);
}
