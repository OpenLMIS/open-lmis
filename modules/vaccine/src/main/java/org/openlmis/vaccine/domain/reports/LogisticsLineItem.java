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

package org.openlmis.vaccine.domain.reports;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Product;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LogisticsLineItem extends BaseModel{

  private Long reportId;
  private Long productId;
  private String productCode;
  private String productName;
  private String productCategory;

  private Product product;

  private Integer displayOrder;

  private Long openingBalance;
  private Long quantityReceived;
  private Long quantityIssued;
  private Long closingBalance;
  private Long quantityVvmAlerted;
  private Long quantityFreezed;
  private Long quantityExpired;
  private Long quantityDiscardedUnopened;
  private Long quantityDiscardedOpened;
  private Long quantityWastedOther;
  private Long daysStockedOut;

  private Long discardingReasonId;
  private String discardingReasonExplanation;

  private String remarks;

  private Long endingBalance;
}
