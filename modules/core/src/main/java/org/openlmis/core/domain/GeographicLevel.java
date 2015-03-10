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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * GeographicLevel represents the real world geographic level at which any facility is located for eg. Country, State, Province etc. Also
 * provides validation methods on geographic level like if the level is root level, is level higher than other given level.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
public class GeographicLevel extends BaseModel {
  String code;
  String name;
  Integer levelNumber;
  private static Integer ROOT_LEVEL_NUMBER = 1;

  public GeographicLevel(Long id, String code, String name, Integer levelNumber) {
    this(code, name, levelNumber);
    this.id = id;
  }

  public GeographicLevel(Long id) {
    this.id = id;
  }

  @JsonIgnore
  public boolean isRootLevel() {
    return this.levelNumber.equals(ROOT_LEVEL_NUMBER);
  }

  @JsonIgnore
  public boolean isLowerInHierarchyThan(GeographicLevel level) {
    return this.getLevelNumber() > level.getLevelNumber();
  }
}

