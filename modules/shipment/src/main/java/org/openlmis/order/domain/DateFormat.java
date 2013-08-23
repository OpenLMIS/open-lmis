/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.domain;

import lombok.Getter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.order.serializer.DateFormatSerializer;

@JsonSerialize(using = DateFormatSerializer.class)

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
