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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

/**
 * SupplyLine represents Supply Line entity and its attributes. Also defines the contract for SupplyLine upload.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SupplyLine extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "Supervising Node", nested = "code")
  private SupervisoryNode supervisoryNode;

  @ImportField(name = "Description")
  private String description;

  @ImportField(mandatory = true, name = "Program", nested = "code")
  private Program program;

  @ImportField(mandatory = true, name = "Facility", nested = "code")
  private Facility supplyingFacility;

  @ImportField(mandatory = true, type = "boolean", name = "Export Orders")
  private Boolean exportOrders;

  private Long parentId;

  public SupplyLine(Long id) {
    this.id = id;
  }
}
