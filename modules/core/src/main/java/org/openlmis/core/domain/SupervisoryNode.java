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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

/**
 * SupervisoryNode represents the Supervisory Node in a system. Also defines contract to upload SupervisoryNode.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonSerialize(include = NON_NULL)
public class SupervisoryNode extends BaseModel implements Importable {

  @ImportField(name = "Supervisory Node Code", mandatory = true)
  private String code;

  @ImportField(name = "Name of Node", mandatory = true)
  private String name;

  @ImportField(name = "Description")
  private String description;

  @ImportField(name = "Parent Node", nested = "code")
  private SupervisoryNode parent;

  @ImportField(name = "Facility Code", mandatory = true, nested = "code")
  private Facility facility;

  public Integer supervisorCount;

  public SupervisoryNode(Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SupervisoryNode that = (SupervisoryNode) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  public void validateParent() {
    if (this.code.equals(this.parent.code) || this.id == this.parent.id)
      throw new DataException("error.supervisory.node.parent.invalid");
  }
}
