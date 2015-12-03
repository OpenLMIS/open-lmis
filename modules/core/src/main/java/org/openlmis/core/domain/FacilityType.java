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
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * FacilityType represents type of a facility and associated attributes, the code and name of facility, the level at which it operates,
 * if it is active or not, etc.
 */
@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class FacilityType extends BaseModel implements Importable {

  @ImportField(name="Facility Type Code", mandatory=true)
  private String code;

  @ImportField(name="Name", mandatory=true)
  private String name;

  @ImportField(name="Description")
  private String description;

  private Integer levelId;

  @ImportField(name="Nominal Max Month", mandatory=true)
  private Integer nominalMaxMonth;

  @ImportField(name="Nominal EOP", mandatory=true)
  private Double nominalEop;

  @ImportField(name="Display Order", mandatory=true)
  private Integer displayOrder;

  @ImportField(name="Active", mandatory=true, type="boolean")
  private boolean active;

  public FacilityType(String code) {
    this.code = code;
  }

  public FacilityType(Long id) {
    this.id = id;
  }
}
