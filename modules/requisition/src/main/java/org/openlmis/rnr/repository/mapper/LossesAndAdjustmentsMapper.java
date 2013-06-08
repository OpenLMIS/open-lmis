/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

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

  @Select("SELECT * FROM losses_adjustments_types ORDER BY displayOrder")
  List<LossesAndAdjustmentsType> getLossesAndAdjustmentsTypes();

  @Delete("DELETE FROM requisition_line_item_losses_adjustments WHERE requisitionLineItemId = #{rnrLineItemId}")
  void deleteByLineItemId(Long rnrLineItemId);
}
