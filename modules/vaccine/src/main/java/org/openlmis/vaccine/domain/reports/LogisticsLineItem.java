/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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
