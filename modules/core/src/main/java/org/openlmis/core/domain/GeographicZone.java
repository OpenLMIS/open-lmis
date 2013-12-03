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
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeographicZone extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "Geographic Zone Code")
  private String code;

  @ImportField(mandatory = true, name = "Geographic Zone Name")
  private String name;

  @ImportField(mandatory = true, name = "Geographic Level Code", nested = "code")
  private GeographicLevel level;

  @ImportField(name = "Geographic Zone Parent Code", nested = "code")
  private GeographicZone parent;

  @ImportField(type = "long", name = "Catchment Population")
  private Long catchmentPopulation;

  @ImportField(type = "double", name = "Geographic Zone LAT")
  private Double latitude;

  @ImportField(type = "double", name = "Geographic Zone LONG")
  private Double longitude;

  public GeographicZone(Long id, String code, String name, GeographicLevel level, GeographicZone parent) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.level = level;
    this.parent = parent;
  }

  public boolean isParentValid() {
    return level.isLowerInHierarchyThan(parent.getLevel());
  }

  public boolean isRootLevel() {
    return level.isRootLevel();
  }
}
