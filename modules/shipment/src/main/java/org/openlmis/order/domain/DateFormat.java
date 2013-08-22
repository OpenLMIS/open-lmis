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

  DATE_1("ddMMyyyy", true),
  DATE_2("dd/MM/yyyy", true),
  DATE_3("dd-MM-yyyy", true),
  DATE_4("dd/MM/yy", true),
  DATE_5("MMddyy", true),

  DATE_6("MM/yy", false),
  DATE_7("yyyy/MM/dd", false);

  @Getter
  private final String format;
  @Getter
  private final boolean orderDate;

  DateFormat(String format, boolean orderDate) {
    this.format = format;
    this.orderDate = orderDate;
  }

}
