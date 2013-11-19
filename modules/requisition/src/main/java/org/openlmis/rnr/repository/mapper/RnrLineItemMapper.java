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
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RnrLineItemMapper {

  @Insert({"INSERT INTO requisition_line_items",
    "(rnrId, productCode, product, productDisplayOrder, productCategory, productCategoryDisplayOrder, beginningBalance,",
    "quantityReceived, quantityDispensed, dispensingUnit,dosesPerMonth, dosesPerDispensingUnit, maxMonthsOfStock,",
    "totalLossesAndAdjustments, packsToShip, packSize, price, roundToZero, packRoundingThreshold, fullSupply,",
    "newPatientCount, stockOutDays,",
    "modifiedBy,createdBy)",
    "VALUES (",
    "#{rnrId}, #{productCode}, #{product}, #{productDisplayOrder}, #{productCategory}, #{productCategoryDisplayOrder}, #{beginningBalance},",
    "#{quantityReceived}, #{quantityDispensed}, #{dispensingUnit},#{dosesPerMonth}, #{dosesPerDispensingUnit}, #{maxMonthsOfStock},",
    "#{totalLossesAndAdjustments}, #{packsToShip}, #{packSize}, #{price},#{roundToZero}, #{packRoundingThreshold}, #{fullSupply},",
    "#{newPatientCount}, #{stockOutDays},",
    "#{modifiedBy}, #{createdBy})"})
  @Options(useGeneratedKeys = true)
  public Integer insert(RnrLineItem rnrLineItem);

  @Select("SELECT * FROM requisition_line_items WHERE rnrId = #{rnrId} and fullSupply = true order by id")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "lossesAndAdjustments", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.LossesAndAdjustmentsMapper.getByRnrLineItem"))
  })
  public List<RnrLineItem> getRnrLineItemsByRnrId(Long rnrId);

  @Update({"UPDATE requisition_line_items",
      "SET quantityReceived = #{quantityReceived},",
      "quantityDispensed = #{quantityDispensed},",
      "beginningBalance = #{beginningBalance},",
      "stockInHand = #{stockInHand},",
      "quantityRequested = #{quantityRequested},",
      "reasonForRequestedQuantity = #{reasonForRequestedQuantity},",
      "totalLossesAndAdjustments = #{totalLossesAndAdjustments},",
      "calculatedOrderQuantity = #{calculatedOrderQuantity},",
      "quantityApproved = #{quantityApproved},",
      "newPatientCount = #{newPatientCount},",
      "stockOutDays = #{stockOutDays},",
      "normalizedConsumption = #{normalizedConsumption},",
      "amc = #{amc},",
      "maxStockQuantity = #{maxStockQuantity},",
      "packsToShip = #{packsToShip},",
      "remarks = #{remarks},",
      "expirationDate = #{expirationDate},",
      "skipped = #{skipped},",
      "modifiedBy = #{modifiedBy},",
      "modifiedDate = CURRENT_TIMESTAMP",
      "WHERE id = #{id}"
  })
  int update(RnrLineItem rnrLineItem);

  @Insert({"INSERT INTO requisition_line_items",
      "(rnrId, productCode, product, productDisplayOrder, productCategory, productCategoryDisplayOrder, dispensingUnit,",
      "dosesPerMonth, dosesPerDispensingUnit, maxMonthsOfStock, packSize, price, roundToZero,",
      "packRoundingThreshold, fullSupply, modifiedBy, quantityReceived, quantityDispensed, beginningBalance,",
      "stockInHand, totalLossesAndAdjustments, calculatedOrderQuantity, quantityApproved,",
      "newPatientCount, stockOutDays, normalizedConsumption, amc, maxStockQuantity,",
      "remarks, quantityRequested, reasonForRequestedQuantity)",
      "VALUES ( ",
      "#{rnrId}, #{productCode}, #{product}, #{productDisplayOrder}, #{productCategory}, #{productCategoryDisplayOrder}, #{dispensingUnit},",
      "#{dosesPerMonth}, #{dosesPerDispensingUnit}, #{maxMonthsOfStock},#{packSize}, #{price}, #{roundToZero},",
      "#{packRoundingThreshold}, #{fullSupply}, #{modifiedBy}, 0, 0, 0,",
      "0, 0, 0, #{quantityApproved},",
      "0, 0, 0, 0, 0,",
      " #{remarks}, #{quantityRequested}, #{reasonForRequestedQuantity})"})
  @Options(useGeneratedKeys = true)
  void insertNonFullSupply(RnrLineItem requisitionLineItem);

  @Select("SELECT * FROM requisition_line_items WHERE rnrId = #{rnrId} AND fullSupply = false")
  public List<RnrLineItem> getNonFullSupplyRnrLineItemsByRnrId(Long rnrId);


  @Delete("DELETE FROM requisition_line_items WHERE rnrId = #{rnrId} AND fullSupply = false")
  void deleteAllNonFullSupplyForRequisition(Long rnrId);

  @Select(
      "SELECT COUNT(DISTINCT productCategory) FROM requisition_line_items WHERE rnrId=#{rnr.id} AND fullSupply = #{isFullSupply}")
  public Integer getCategoryCount(@Param(value = "rnr") Rnr rnr, @Param(value = "isFullSupply") Boolean isFullSupply);

  @Update("UPDATE requisition_line_items " +
      "SET quantityApproved = #{quantityApproved}, " +
      " packsToShip = #{packsToShip}, " +
      " remarks = #{remarks}, " +
      " modifiedBy = #{modifiedBy}, " +
      " modifiedDate = CURRENT_TIMESTAMP " +
      " WHERE id = #{id}"
  )
  void updateOnApproval(RnrLineItem lineItem);

  @Select("SELECT * FROM requisition_line_items WHERE rnrId = #{rnrId} AND productCode = #{productCode} AND fullSupply = false")
  RnrLineItem getExistingNonFullSupplyItemByRnrIdAndProductCode(@Param(value = "rnrId") Long rnrId, @Param(value = "productCode") String productCode);

  @Select({"SELECT RSC.createdDate AS authorizedDate, RLI.productCode",
      "FROM requisition_status_changes RSC INNER JOIN requisitions R ON RSC.rnrId = R.id",
      "INNER JOIN requisition_line_items RLI ON R.id = RLI.rnrId",
      "WHERE RSC.status = 'AUTHORIZED' AND",
      "RLI.skipped = false AND",
      "R.facilityId = #{rnr.facility.id} AND",
      "R.programId = #{rnr.program.id} AND",
      "RSC.createdDate >= #{periodStartDate} AND",
      "RLI.productCode = #{productCode}",
      "ORDER BY RSC.createdDate DESC LIMIT 1"
  })
  Date getCreatedDateForPreviousLineItem(@Param("rnr") Rnr rnr, @Param("productCode") String productCode, @Param("periodStartDate") Date periodStartDate);

}
