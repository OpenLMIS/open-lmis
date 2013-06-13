/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;

import java.util.Date;

import static org.apache.commons.lang.StringUtils.isBlank;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProcessingPeriod extends BaseModel {

  private Long scheduleId;

  private String name;
  private String description;
  private Date startDate;
  private Date endDate;
  private Integer numberOfMonths;

  public ProcessingPeriod(Long id) {
    this.id = id;
  }

  public ProcessingPeriod(Long id, Date startDate, Date endDate, Integer numberOfMonths, String name) {
    this.id = id;
    this.startDate = startDate;
    this.endDate = endDate;
    this.numberOfMonths = numberOfMonths;
    this.name = name;
  }

  public void validate() {
    if (scheduleId == null) {
      throw new DataException("error.period.without.schedule");
    }
    if (startDate == null || startDate.toString().isEmpty()) {
      throw new DataException("error.period.without.start.date");
    }
    if (endDate == null || endDate.toString().isEmpty()) {
      throw new DataException("error.period.without.end.date");
    }
    if (isBlank(name)) {
      throw new DataException("error.period.without.name");
    }
    if (endDate.compareTo(startDate) <= 0) {
      throw new DataException("error.period.invalid.dates");
    }
  }

  public ProcessingPeriod basicInformation() {
    return new ProcessingPeriod(id, startDate, endDate, numberOfMonths, name);
  }
}
