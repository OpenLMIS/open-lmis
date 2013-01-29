package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RnrLineItemMapper {

  @Insert({"INSERT INTO requisition_line_items",
    "(rnrId, productCode, product, beginningBalance, dispensingUnit, dosesPerMonth, dosesPerDispensingUnit,",
    "maxMonthsOfStock, packsToShip, packSize, price, roundToZero, packRoundingThreshold,",
    "fullSupply, previousStockInHandAvailable, modifiedBy, modifiedDate)",
    "VALUES (" +
      "#{rnrId}, #{productCode}, #{product}, #{beginningBalance}, #{dispensingUnit}, #{dosesPerMonth},",
      "#{dosesPerDispensingUnit}, #{maxMonthsOfStock}, #{packsToShip},",
      "#{packSize}, #{price}, #{roundToZero}, #{packRoundingThreshold},",
      "#{fullSupply}, #{previousStockInHandAvailable}, #{modifiedBy}, #{modifiedDate})"})
  @Options(useGeneratedKeys = true)
  public Integer insert(RnrLineItem rnrLineItem);

  @Select("SELECT * FROM requisition_line_items WHERE rnrId = #{rnrId}")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "lossesAndAdjustments", javaType =List.class, column = "id", many = @Many(select = "org.openlmis.rnr.repository.mapper.LossesAndAdjustmentsMapper.getByRnrLineItem"))
  })
    public List<RnrLineItem> getRnrLineItemsByRnrId(Integer rnrId);

  @Update("UPDATE requisition_line_items " +
    "SET quantityReceived = #{quantityReceived}, " +
    " quantityDispensed = #{quantityDispensed}, " +
    " beginningBalance = #{beginningBalance}, " +
    " stockInHand = #{stockInHand}, " +
    " quantityRequested = #{quantityRequested}, " +
    " reasonForRequestedQuantity = #{reasonForRequestedQuantity}, " +
    " totalLossesAndAdjustments = #{totalLossesAndAdjustments}, " +
    " calculatedOrderQuantity = #{calculatedOrderQuantity}, " +
    " quantityApproved = #{quantityApproved}, " +
    " newPatientCount = #{newPatientCount}, " +
    " stockOutDays = #{stockOutDays}, " +
    " normalizedConsumption = #{normalizedConsumption}, " +
    " amc = #{amc}, " +
    " maxStockQuantity = #{maxStockQuantity}, " +
    " packsToShip = #{packsToShip}, " +
    " remarks = #{remarks}, " +
    " modifiedBy = #{modifiedBy}, " +
    " modifiedDate = DEFAULT " +
    "WHERE id = #{id}"
  )
  int update(RnrLineItem rnrLineItem);

  @Insert({"INSERT INTO requisition_line_items",
    "(rnrId, productCode, product, dispensingUnit, dosesPerMonth, dosesPerDispensingUnit,",
    "maxMonthsOfStock, packsToShip, packSize, price, roundToZero, packRoundingThreshold,",
    "fullSupply, modifiedBy, modifiedDate, quantityReceived, quantityDispensed, beginningBalance," ,
      "stockInHand, totalLossesAndAdjustments, calculatedOrderQuantity, quantityApproved," ,
      "newPatientCount, stockOutDays, normalizedConsumption, amc, maxStockQuantity, remarks, quantityRequested, reasonForRequestedQuantity)",
    "VALUES ( #{rnrId}, #{productCode}, #{product}, #{dispensingUnit}, #{dosesPerMonth},",
    "#{dosesPerDispensingUnit}, #{maxMonthsOfStock}, #{packsToShip},",
    "#{packSize}, #{price}, #{roundToZero}, #{packRoundingThreshold},",
    "#{fullSupply}, #{modifiedBy}, #{modifiedDate}, #{quantityReceived}, #{quantityDispensed}, #{beginningBalance},",
      "#{stockInHand}, #{totalLossesAndAdjustments}, #{calculatedOrderQuantity}, #{quantityApproved},",
      "#{newPatientCount}, #{stockOutDays}, #{normalizedConsumption}, #{amc}, #{maxStockQuantity}," ,
        " #{remarks}, #{quantityRequested}, #{reasonForRequestedQuantity})"})
  @Options(useGeneratedKeys = true)
  void insertNonFullSupply(RnrLineItem requisitionLineItem);
}
