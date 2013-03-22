/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingSchedule {
  private Integer id;

  private String code;
  private String name;
  private String description;
  private Integer modifiedBy;
  private Date modifiedDate;
  public static final String SCHEDULE_WITHOUT_CODE = "schedule.without.code";
  public static final String SCHEDULE_WITHOUT_NAME = "schedule.without.name";

  public ProcessingSchedule(String code, String name) {
    this(code, name, null);
  }

  public ProcessingSchedule(String code, String name, String description) {
    this(null, code, name, description, null, null);
  }

  public void validate() {
    if (code == null || code.isEmpty()) {
      throw new DataException(SCHEDULE_WITHOUT_CODE);
    }
    if (name == null || name.isEmpty()) {
      throw new DataException(SCHEDULE_WITHOUT_NAME);
    }
  }
}
