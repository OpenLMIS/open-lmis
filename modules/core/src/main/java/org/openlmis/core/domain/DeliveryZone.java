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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * DeliveryZone represents DeliveryZone entity, with its basic attributes code, name and description.
 * It also defines the contract for entity creation/upload, eg. code is mandatory and upload header for code is "Delivery zone code".
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class DeliveryZone extends BaseModel implements Importable {

  @ImportField(name = "Delivery zone code", mandatory = true)
  String code;
  @ImportField(name = "Delivery zone name", mandatory = true)
  String name;
  @ImportField(name = "Description")
  String description;

  public DeliveryZone(Long id) {
    this.id = id;
  }
}
