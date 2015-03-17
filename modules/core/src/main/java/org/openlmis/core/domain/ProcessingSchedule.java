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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.openlmis.core.exception.DataException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * ProcessingSchedule represents time schedule requisition life cycle will follow. Each processing schedule consists
 * of periods for which requisitions are initiated and submitted.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
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

  @SuppressWarnings("unused")
  public String getStringModifiedDate() throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    return this.modifiedDate == null ? null : simpleDateFormat.format(this.modifiedDate);
  }

}
