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

/**
 * It maps the RnrLineItem entity to corresponding representation in database.
 */

@Repository
public interface RnrLineItemMapper {

  @Insert({"INSERT INTO requisition_line_items",
    "(rnrId, productCode, product, productDisplayOrder, productCategory, productCategoryDisplayOrder, previousStockInHand, beginningBalance,",
    "quantityReceived, quantityDispensed, dispensingUnit,dosesPerMonth, dosesPerDispensingUnit, maxMonthsOfStock,",
    "totalLossesAndAdjustments, packsToShip, packSize, price, roundToZero, packRoundingThreshold, fullSupply,",
    "newPatientCount, stockOutDays, previousNormalizedConsumptions, reportingDays, skipped, ",
    "modifiedBy,createdBy)",
    "VALUES (",
    "#{lineItem.rnrId}, #{lineItem.productCode}, #{lineItem.product}, #{lineItem.productDisplayOrder}, #{lineItem.productCategory},",
    "#{lineItem.productCategoryDisplayOrder}, #{lineItem.previousStockInHand}, #{lineItem.beginningBalance}, #{lineItem.quantityReceived}, #{lineItem.quantityDispensed},",
    "#{lineItem.dispensingUnit},#{lineItem.dosesPerMonth}, #{lineItem.dosesPerDispensingUnit}, #{lineItem.maxMonthsOfStock},",
    "#{lineItem.totalLossesAndAdjustments}, #{lineItem.packsToShip}, #{lineItem.packSize}, #{lineItem.price},#{lineItem.roundToZero},",
    "#{lineItem.packRoundingThreshold}, #{lineItem.fullSupply}, #{lineItem.newPatientCount}, #{lineItem.stockOutDays},",
    "#{previousNormalizedConsumptions}, #{lineItem.reportingDays}, #{lineItem.skipped} , #{lineItem.modifiedBy}, #{lineItem.createdBy})"})
  @Options(useGeneratedKeys = true, keyProperty = "lineItem.id")
  public Integer insert(@Param("lineItem") RnrLineItem rnrLineItem, @Param("previousNormalizedConsumptions") String previousNormalizedConsumptions);

  @Select({"SELECT requisition_line_items.*, products.strength, products.primaryname ",
          "FROM requisition_line_items, products, program_products",
          "WHERE rnrId = #{rnrId} and requisition_line_items.fullSupply = true",
          "and requisition_line_items.productcode = products.code",
          "and products.id = program_products.productid",
          "order by program_products.displayorder;"})
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "productStrength", column = "strength"),
    @Result(property = "productPrimaryName", column = "primaryname"),
    @Result(property = "previousNormalizedConsumptions", column = "previousNormalizedConsumptions", typeHandler = StringToList.class),
    @Result(property = "lossesAndAdjustments", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.rnr.repository.mapper.LossesAndAdjustmentsMapper.getByRnrLineItem"))
  })
  public List<RnrLineItem> getRnrLineItemsByRnrId(Long rnrId);

  @Update({"UPDATE requisition_line_items",
    "SET quantityReceived = #{quantityReceived},",
    "quantityDispensed = #{quantityDispensed},",
    "previousStockInHand = #{previousStockInHand},",
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
    "periodNormalizedConsumption = #{periodNormalizedConsumption},",
    "amc = #{amc},",
    "maxStockQuantity = #{maxStockQuantity},",
    "packsToShip = #{packsToShip},",
    "remarks = #{remarks},",
    "reportingDays = #{reportingDays},",
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

  @Select("SELECT COUNT(DISTINCT productCategory) FROM requisition_line_items WHERE rnrId=#{rnr.id} AND fullSupply = #{isFullSupply}")
  public Integer getCategoryCount(@Param(value = "rnr") Rnr rnr, @Param(value = "isFullSupply") Boolean isFullSupply);

  @Update("UPDATE requisition_line_items " +
    "SET quantityApproved = #{quantityApproved}, " +
    " packsToShip = #{packsToShip}, " +
    " skipped = #{skipped}, " +
    " remarks = #{remarks}, " +
    " modifiedBy = #{modifiedBy}, " +
    " modifiedDate = CURRENT_TIMESTAMP " +
    " WHERE id = #{id}"
  )
  void updateOnApproval(RnrLineItem lineItem);

  @Select("SELECT * FROM requisition_line_items WHERE rnrId = #{rnrId} AND productCode = #{productCode} AND fullSupply = false")
  RnrLineItem getExistingNonFullSupplyItemByRnrIdAndProductCode(@Param(value = "rnrId") Long rnrId,
                                                                @Param(value = "productCode") String productCode);

  @Select({"SELECT RSC.createdDate FROM requisition_status_changes RSC INNER JOIN requisitions",
    "R ON RSC.rnrId = R.id AND RSC.status = 'AUTHORIZED'",
    "AND R.facilityId = #{rnr.facility.id}",
    "AND R.programId = #{rnr.program.id}",
    "AND RSC.createdDate >= #{periodStartDate}",
    "INNER JOIN requisition_line_items RLI ON R.id = RLI.rnrId",
    "AND RLI.skipped = false",
    "AND RLI.productCode = #{productCode}",
    "ORDER BY RSC.createdDate DESC LIMIT 1"})
  Date getAuthorizedDateForPreviousLineItem(@Param("rnr") Rnr rnr,
                                            @Param("productCode") String productCode,
                                            @Param("periodStartDate") Date periodStartDate);

  @Select({"SELECT RLI.normalizedConsumption, RLI.stockInHand FROM requisition_line_items RLI",
    "INNER JOIN requisitions R ON R.id = RLI.rnrId",
    "AND R.facilityId = #{rnr.facility.id}",
    "AND R.programId = #{rnr.program.id}",
    "AND RLI.productCode = #{productCode}",
    "INNER JOIN requisition_status_changes",
    "RSC ON RSC.rnrId = R.id",
    "AND RLI.skipped = false",
    "AND RSC.status = 'AUTHORIZED'",
    "AND R.emergency = false",
    "AND RSC.createdDate >= #{startDate}",
    "ORDER BY RSC.createdDate DESC LIMIT #{count}"})
  List<RnrLineItem> getAuthorizedRegularUnSkippedLineItems(@Param("productCode") String productCode,
                                                           @Param("rnr") Rnr rnr, @Param("count") Integer count,
                                                           @Param("startDate") Date startDate);

  @Select("SELECT * FROM requisition_line_items WHERE rnrId = #{rnrId} AND productCode = #{productCode} AND skipped = FALSE")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "previousNormalizedConsumptions", column = "previousNormalizedConsumptions", typeHandler = StringToList.class),
    @Result(property = "lossesAndAdjustments", javaType = List.class, column = "id",
      many = @Many(select = "org.openlmis.rnr.repository.mapper.LossesAndAdjustmentsMapper.getByRnrLineItem"))
  })
  RnrLineItem getNonSkippedLineItem(@Param("rnrId") Long rnrId, @Param("productCode") String productCode);

  @Select({"SELECT productCode, beginningBalance, quantityReceived, quantityDispensed, ",
      "stockInHand, quantityRequested, calculatedOrderQuantity, quantityApproved, ",
      "totalLossesAndAdjustments, expirationDate",
      "FROM requisition_line_items",
      "WHERE rnrId = #{rnrId} and fullSupply = TRUE",
      "AND skipped = FALSE"})
  List<RnrLineItem> getNonSkippedRnrLineItemsByRnrId(Long rnrId);

  @Select({"SELECT productCode, beginningBalance, quantityReceived, quantityDispensed, ",
      "stockInHand, quantityRequested, calculatedOrderQuantity, quantityApproved, ",
      "totalLossesAndAdjustments, expirationDate",
      "FROM requisition_line_items",
      "WHERE rnrId = #{rnrId} and fullSupply = FALSE",
      "AND skipped = FALSE"})
  List<RnrLineItem> getNonSkippedNonFullSupplyRnrLineItemsByRnrId(Long rnrId);
}
