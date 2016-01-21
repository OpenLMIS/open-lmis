/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.equipment.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.domain.BaseModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "equipmentTypeName")
@JsonSubTypes({@JsonSubTypes.Type(value = ColdChainEquipment.class, name = "coldChainEquipment"),
    @JsonSubTypes.Type(value = Equipment.class, name = "equipment")
})
public class Equipment extends BaseModel {

  private String name;

  private EquipmentType equipmentType;

  private Long equipmentTypeId;

  private String manufacturer;

  private String model;

  private EquipmentEnergyType energyType;

  private Long energyTypeId;

  private Integer inventoryCount;

  public boolean equalsByMakeAndModel(Equipment other) {
    return other.manufacturer != null && other.model != null
        && other.manufacturer.equalsIgnoreCase(manufacturer) && other.model.equalsIgnoreCase(model);
  }

  public boolean isRemovable() {
    return inventoryCount != null && inventoryCount == 0;
  }

}
