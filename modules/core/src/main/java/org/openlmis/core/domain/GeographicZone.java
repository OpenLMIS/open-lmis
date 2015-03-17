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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * GeographicZone represents a real world entity for Geographic Zone. Also defines the contract for creation/upload of Geographic zone entity.
 * This class also provides utility methods like finding the level for zone, if the zone parent is higher in hierarchy etc.
 */
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

  @JsonIgnore
  public boolean isParentHigherInHierarchy() {
    return level.isLowerInHierarchyThan(parent.getLevel());
  }

  @JsonIgnore
  public boolean isRootLevel() {
    return level.isRootLevel();
  }

  @JsonIgnore
  public void validateLevel() {
    if (this.getLevel() == null)
      throw new DataException("error.geo.level.invalid");

    validateLevelAndParentAssociation();
  }

  @JsonIgnore
  public void validateParentIsHigherInHierarchy() {
    if (!this.isParentHigherInHierarchy()) {
      throw new DataException("error.invalid.hierarchy");
    }
  }

  @JsonIgnore
  public void validateParentExists() {
    if (parent == null) {
      throw new DataException("error.geo.zone.parent.invalid");
    }
  }

  private void validateLevelAndParentAssociation() {
    if (this.parent == null && !this.isRootLevel()) {
      throw new DataException("error.invalid.hierarchy");
    }

    if (this.parent != null && this.isRootLevel()) {
      throw new DataException("error.invalid.hierarchy");
    }
  }

  public void validateMandatoryFields() {
    if (isBlank(this.code) || isBlank(this.name)) {
      throw new DataException("error.mandatory.fields.missing");
    }
  }
}
