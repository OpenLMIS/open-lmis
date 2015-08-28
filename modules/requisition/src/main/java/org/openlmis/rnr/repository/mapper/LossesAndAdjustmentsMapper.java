/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * It maps the LossesAndAdjustments entity to corresponding representation in database.
 */

@Repository
public interface LossesAndAdjustmentsMapper {

  @Insert("INSERT INTO requisition_line_item_losses_adjustments(requisitionLineItemId, type, quantity, modifiedBy) " +
      "VALUES(#{rnrLineItem.id}, #{lossesAndAdjustments.type.name}, #{lossesAndAdjustments.quantity}, #{rnrLineItem.modifiedBy})")
  public Integer insert(@Param(value = "rnrLineItem") RnrLineItem rnrLineItem, @Param(value = "lossesAndAdjustments") LossesAndAdjustments lossesAndAdjustments);


  @Select("select * from requisition_line_item_losses_adjustments where requisitionLineItemId = #{rnrLineItemId}")
  @Results(value = {
      @Result(property = "type", column = "type", javaType = String.class, one = @One(select = "getLossesAndAdjustmentTypeByName"))
  })
  List<LossesAndAdjustments> getByRnrLineItem(Long rnrLineItemId);

  @Select("SELECT * FROM losses_adjustments_types WHERE name = #{lossesAndAdjustmentsTypeName}")
  LossesAndAdjustmentsType getLossesAndAdjustmentTypeByName(String lossesAndAdjustmentsTypeName);

  @Delete("DELETE FROM requisition_line_item_losses_adjustments WHERE id = #{lossesAndAdjustmentsId}")
  void delete(Long lossesAndAdjustmentsId);

  @Select("SELECT * FROM losses_adjustments_types WHERE isdefault = TRUE ORDER BY displayOrder")
  List<LossesAndAdjustmentsType> getLossesAndAdjustmentsTypes();

  @Delete("DELETE FROM requisition_line_item_losses_adjustments WHERE requisitionLineItemId = #{rnrLineItemId}")
  void deleteByLineItemId(Long rnrLineItemId);
}
