/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegimenColumn extends Column {

  private Long programId;

  private String dataType;

  public RegimenColumn(Long programId, String name, String label, String dataType, Boolean visible, Long createdBy) {
    super(name, label, visible);
    this.programId = programId;
    this.dataType = dataType;
    this.createdBy = createdBy;
  }

  @Override
  public Integer getColumnWidth() {
    if (this.name.equals("remarks")) {
      return 80;
    }
    return 40;
  }

  @Override
  public ColumnType getColumnType() {
    if (this.getName().equals("name") || this.getName().equals("code") || this.getName().equals("remarks")) {
      return ColumnType.TEXT;
    } else {
      return ColumnType.NUMERIC;
    }
  }

}
