/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.domain;

/**
 * Enum for Date Formats. Date formats used for Order Date have orderDate attribute set to true by default.
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DateFormat {

  DATE_1("MM/yy", false),
  DATE_2("MM/yyyy", false),
  DATE_3("yy/MM", false),
  DATE_4("yyyy/MM", false),
  DATE_5("dd/MM/yy", true),
  DATE_6("dd/MM/yyyy", true),
  DATE_7("MM/dd/yy", true),
  DATE_8("MM/dd/yyyy", true),
  DATE_9("yy/MM/dd", true),
  DATE_10("yyyy/MM/dd", true),
  DATE_11("MM-yy", false),
  DATE_12("MM-yyyy", false),
  DATE_13("yy-MM", false),
  DATE_14("yyyy-MM", false),
  DATE_15("dd-MM-yy", true),
  DATE_16("dd-MM-yyyy", true),
  DATE_17("MM-dd-yy", true),
  DATE_18("MM-dd-yyyy", true),
  DATE_19("yy-MM-dd", true),
  DATE_20("yyyy-MM-dd", true),
  DATE_21("MMyy", false),
  DATE_22("MMyyyy", false),
  DATE_23("yyMM", false),
  DATE_24("yyyyMM", false),
  DATE_25("ddMMyy", true),
  DATE_26("ddMMyyyy", true),
  DATE_27("MMddyy", true),
  DATE_28("MMddyyyy", true),
  DATE_29("yyMMdd", true),
  DATE_30("yyyyMMdd", true);

  @Getter
  private final String format;
  @Getter
  private final boolean orderDate;

  DateFormat(String format, boolean orderDate) {
    this.format = format;
    this.orderDate = orderDate;
  }

}
