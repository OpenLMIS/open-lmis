package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.LossesAndAdjustmentsTypeEnum;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public interface LossesAndAdjustmentsMapper {

    @Select("INSERT INTO requisition_line_item_losses_adjustments(requisitionLineItemId, type, quantity) " +
            "VALUES(#{rnrLineItem.id}, #{lossesAndAdjustments.type.name}, #{lossesAndAdjustments.quantity}) RETURNING id")
    @Options(useGeneratedKeys = true)
    public Integer insert(@Param(value = "rnrLineItem") RnrLineItem rnrLineItem, @Param(value = "lossesAndAdjustments") LossesAndAdjustments lossesAndAdjustments);


    @Select("select * from requisition_line_item_losses_adjustments where requisitionLineItemId = #{rnrLineItemId}")
    @Results(value = {
            @Result(property = "type", column = "type", javaType = String.class, one = @One(select = "getLossesAndAdjustmentTypeByName"))
    })
    List<LossesAndAdjustments> getByRnrLineItem(Integer rnrLineItemId);


    @Select("SELECT * FROM losses_adjustments_types WHERE name = #{name}")
    LossesAndAdjustmentsType getLossesAndAdjustmentTypeByName(LossesAndAdjustmentsTypeEnum lossesAndAdjustmentsType);

    @Delete("DELETE FROM requisition_line_item_losses_adjustments WHERE id = #{lossesAndAdjustmentsId}")
    void delete(Integer lossesAndAdjustmentsId);

    @Update("UPDATE requisition_line_item_losses_adjustments " +
            "SET quantity = #{lossesAndAdjustments.quantity}, " +
            "type = #{lossesAndAdjustments.type.name} " +
            "WHERE requisitionLineItemId = #{rnrLineItem.id} ")
    public Integer update(@Param(value = "rnrLineItem") RnrLineItem rnrLineItem, @Param(value = "lossesAndAdjustments") LossesAndAdjustments lossesAndAdjustments);
}
