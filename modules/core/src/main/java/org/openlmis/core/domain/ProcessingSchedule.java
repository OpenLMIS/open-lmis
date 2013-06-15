/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProcessingSchedule extends BaseModel {
  private String code;
  private String name;
  private String description;

  public ProcessingSchedule(String code, String name) {
    this(code, name, null);
  }

  public ProcessingSchedule(Long id) {
    this.id = id;
  }

  public void validate() {
    if (code == null || code.isEmpty()) {
      throw new DataException("schedule.without.code");
    }
    if (name == null || name.isEmpty()) {
      throw new DataException("schedule.without.name");
    }
  }
}
