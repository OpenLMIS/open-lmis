package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RnrLineItemMapper {

    @Select("insert into requisition_line_item(rnr_id, product_code, product, doses_per_month, doses_per_dispensing_unit, modified_by, modified_date) " +
            "values (#{rnrId}, #{productCode}, #{product}, #{dosesPerMonth}, #{dosesPerDispensingUnit}, #{modifiedBy}, #{modifiedDate}) returning id")
    @Options(useGeneratedKeys=true)
    public Integer insert(RnrLineItem rnrLineItem);

    @Select("Select * from requisition_line_item where rnr_id = #{rnrId}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "rnrId", column = "rnr_id"),
            @Result(property = "product", column = "product"),
            @Result(property = "productCode", column = "product_code"),
            @Result(property = "quantityReceived", column = "quantity_received"),
            @Result(property = "quantityDispensed", column = "quantity_dispensed"),
            @Result(property = "beginningBalance", column = "beginning_balance"),
            @Result(property = "estimatedConsumption", column = "estimated_consumption"),
            @Result(property = "stockInHand", column = "stock_in_hand"),
            @Result(property = "quantityRequested", column = "quantity_requested"),
            @Result(property = "reasonForRequestedQuantity", column = "reason_for_requested_quantity"),
            @Result(property = "calculatedOrderQuantity", column = "calculated_order_quantity"),
            @Result(property = "quantityApproved", column = "quantity_approved"),
            @Result(property = "lossesAndAdjustments", column = "losses_and_adjustments"),
            @Result(property = "reasonForLossesAndAdjustments", column = "reason_for_losses_and_adjustments"),
            @Result(property = "newPatientCount", column = "new_patient_count"),
            @Result(property = "stockOutDays", column = "stock_out_days"),
            @Result(property = "normalizedConsumption", column = "normalized_consumption"),
            @Result(property = "amc", column = "amc"),
            @Result(property = "maxStockQuantity", column = "max_stock_quantity"),
            @Result(property = "packsToShip", column = "packs_to_ship"),
            @Result(property = "cost", column = "cost"),
            @Result(property = "dosesPerMonth", column = "doses_per_month"),
            @Result(property = "dosesPerDispensingUnit", column = "doses_per_dispensing_unit"),
            @Result(property = "remarks", column = "remarks"),
            @Result(property = "modifiedBy", column = "modified_by"),
            @Result(property = "modifiedDate", column = "modified_date")
    })
    public List<RnrLineItem> getRnrLineItemsByRnrId(Integer rnrId);

    
    @Update("update requisition_line_item " +
            "set quantity_received = #{quantityReceived}, "+
            " quantity_dispensed = #{quantityDispensed}, "+
            " beginning_balance = #{beginningBalance}, "+
            " estimated_consumption = #{estimatedConsumption}, "+
            " stock_in_hand = #{stockInHand}, "+
            " quantity_requested = #{quantityRequested}, "+
            " reason_for_requested_quantity = #{reasonForRequestedQuantity}, "+
            " calculated_order_quantity = #{calculatedOrderQuantity}, "+
            " quantity_approved = #{quantityApproved}, "+
            " losses_and_adjustments = #{lossesAndAdjustments}, "+
            " reason_for_losses_and_adjustments = #{reasonForLossesAndAdjustments}, "+
            " new_patient_count = #{newPatientCount}, "+
            " stock_out_days = #{stockOutDays}, "+
            " normalized_consumption = #{normalizedConsumption}, "+
            " amc = #{amc}, "+
            " max_stock_quantity = #{maxStockQuantity}, "+
            " packs_to_ship = #{packsToShip}, "+
            " cost = #{cost}, "+
            " remarks = #{remarks}, "+
            " modified_by = #{modifiedBy}, "+
            " modified_date = DEFAULT " +
            "where id = #{id}"
           )
    void update(RnrLineItem rnrLineItem);
}
