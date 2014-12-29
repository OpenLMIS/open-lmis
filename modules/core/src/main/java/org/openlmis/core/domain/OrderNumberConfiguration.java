/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;

/**
 * OrderNumberConfiguration represents the configuration for order number present in the order file.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderNumberConfiguration {

  private String orderNumberPrefix;
  private Boolean includeOrderNumberPrefix;
  private Boolean includeProgramCode;
  private Boolean includeSequenceCode;
  private Boolean includeRnrTypeSuffix;

  public void validate() {
    if (includeSequenceCode == null || !includeSequenceCode)
      throw new DataException("Sequence code is mandatory");
  }

  public String getOrderNumberFor(Long orderId, Program program, Boolean emergeny) {
    StringBuilder orderNumber = new StringBuilder();

    if (includeOrderNumberPrefix && orderNumberPrefix != null)
      orderNumber.append(getOrderNumberPrefix());
    if (includeProgramCode)
      orderNumber.append(getTruncatedProgramCode(program.getCode()));
    orderNumber.append(getSequenceAppendedOrderId(orderId));
    if (includeRnrTypeSuffix)
      orderNumber.append(emergeny ? "E" : "R");
    return orderNumber.toString();
  }

  private String getSequenceAppendedOrderId(Long orderId) {
    return String.format("%08d", orderId);
  }

  private String getTruncatedProgramCode(String code) {
    return code.length() > 35 ? code.substring(0, 35) : code;
  }
}

