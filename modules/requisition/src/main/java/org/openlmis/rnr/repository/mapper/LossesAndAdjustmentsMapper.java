package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LossesAndAdjustmentsMapper {

  @Insert("INSERT INTO requisition_line_item_losses_adjustments(requisitionLineItemId, type, quantity) " +
      "VALUES(#{rnrLineItem.id}, #{lossesAndAdjustments.type.name}, #{lossesAndAdjustments.quantity})")
  public Integer insert(@Param(value = "rnrLineItem") RnrLineItem rnrLineItem, @Param(value = "lossesAndAdjustments") LossesAndAdjustments lossesAndAdjustments);


  @Select("select * from requisition_line_item_losses_adjustments where requisitionLineItemId = #{rnrLineItemId}")
  @Results(value = {
      @Result(property = "type", column = "type", javaType = String.class, one = @One(select = "getLossesAndAdjustmentTypeByName"))
  })
  List<LossesAndAdjustments> getByRnrLineItem(Integer rnrLineItemId);

  @Select("SELECT * FROM losses_adjustments_types WHERE name = #{lossesAndAdjustmentsTypeName}")
  LossesAndAdjustmentsType getLossesAndAdjustmentTypeByName(String lossesAndAdjustmentsTypeName);

  @Delete("DELETE FROM requisition_line_item_losses_adjustments WHERE id = #{lossesAndAdjustmentsId}")
  void delete(Integer lossesAndAdjustmentsId);

  @Select("SELECT * FROM losses_adjustments_types ORDER BY displayOrder")
  List<LossesAndAdjustmentsType> getLossesAndAdjustmentsTypes();

  @Delete("DELETE FROM requisition_line_item_losses_adjustments WHERE requisitionLineItemId = #{rnrLineItemId}")
  void deleteByLineItemId(Integer rnrLineItemId);
}
