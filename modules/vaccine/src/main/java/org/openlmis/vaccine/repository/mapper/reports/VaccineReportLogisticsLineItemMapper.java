/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    " (reportId, productId, productCode, productName, productCategory, displayOrder, openingBalance, quantityReceived, quantityIssued, closingBalance, quantityVvmAlerted, quantityFreezed, quantityExpired, quantityDiscardedUnopened, quantityDiscardedOpened, quantityWastedOther, daysStockedOut , discardingReasonId, discardingReasonExplanation, remarks, createdBy, createdDate, modifiedBy, modifiedDate)" +
    " values " +
    " (#{reportId}, #{productId}, #{productCode}, #{productName}, #{productCategory} , #{displayOrder}, #{openingBalance}, #{quantityReceived}, #{quantityIssued}, #{closingBalance}, #{quantityVvmAlerted}, #{quantityFreezed}, #{quantityExpired}, #{quantityDiscardedUnopened}, #{quantityDiscardedOpened}, #{quantityWastedOther},  #{daysStockedOut} , #{discardingReasonId}, #{discardingReasonExplanation}, #{remarks}, #{createdBy}, NOW(), #{modifiedBy}, NOW())")
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
