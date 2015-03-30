/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.mapper.reports;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.vaccine.domain.reports.LogisticsLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineReportLogisticsLineItemMapper {

  @Insert("INSERT INTO vaccine_report_logistics_line_items " +
    " (reportId, productId, productCode, productName, productCategory, displayOrder, openingBalance, quantityReceived, quantityIssued, closingBalance, quantityVvmAlerted, quantityFreezed, quantityExpired, quantityDiscardedUnopened, quantityDiscardedOpened, quantityWastedOther, endingBalance, daysStockedOut , discardingReasonId, discardingReasonExplanation, remarks, createdBy, createdDate, modifiedBy, modifiedDate)" +
    " values " +
    " (#{reportId}, #{productId}, #{productCode}, #{productName}, #{productCategory} , #{displayOrder}, #{openingBalance}, #{quantityReceived}, #{quantityIssued}, #{closingBalance}, #{quantityVvmAlerted}, #{quantityFreezed}, #{quantityExpired}, #{quantityDiscardedUnopened}, #{quantityDiscardedOpened}, #{quantityWastedOther}, #{endingBalance} ,  #{daysStockedOut} , #{discardingReasonId}, #{discardingReasonExplanation}, #{remarks}, #{createdBy}, NOW(), #{modifiedBy}, NOW())")
  @Options(useGeneratedKeys = true)
  Integer insert(LogisticsLineItem lineItem);

  @Update("UPDATE vaccine_report_logistics_line_items " +
    " set " +
    " reportId = #{reportId} " +
    ", productId = #{productId} " +
    ", productCode = #{productCode} " +
    ", productCategory = #{productCategory} " +
    ", productName = #{productName} " +
    ", displayOrder = #{displayOrder} " +
    ", openingBalance = #{openingBalance} " +
    ", quantityReceived = #{quantityReceived} " +
    ", daysStockedOut = #{daysStockedOut} " +
    ", discardingReasonExplanation = #{discardingReasonExplanation} " +
    ", discardingReasonId = #{discardingReasonId} " +
    ", remarks = #{remarks} " +
    ", quantityIssued = #{quantityIssued} " +
    ", closingBalance = #{closingBalance} " +
    ", quantityVvmAlerted = #{quantityVvmAlerted}" +
    ", quantityFreezed = #{quantityFreezed} " +
    ", quantityExpired = #{quantityExpired} " +
    ", quantityDiscardedUnopened = #{quantityDiscardedUnopened} " +
    ", quantityDiscardedOpened  = #{quantityDiscardedOpened}" +
    ", quantityWastedOther = #{quantityWastedOther} " +
    ", endingBalance = #{endingBalance} " +
    ", modifiedBy = #{modifiedBy} " +
    ", modifiedDate = NOW()" +
    "WHERE id = #{id} ")
  void update(LogisticsLineItem lineItem);

  @Select("select * from vaccine_report_logistics_line_items where reportId = #{reportId} order by id")
    @Results(value = {
      @Result(property = "productId", column = "productId"),
      @Result(property = "product", column = "productId", javaType = Product.class,
        one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById"))
    })
  List<LogisticsLineItem> getLineItems(@Param("reportId") Long reportId);

}
