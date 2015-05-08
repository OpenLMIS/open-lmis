/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ColdChainEquipment extends Equipment{

  private Equipment equipment;
  private Long equipmentId;
  private ColdChainEquipmentDesignation designation;
  private Long designationId;
  private String brand;
  private String model;
  private String cceCode;
  private String pqsCode;
  private Float refrigeratorCapacity;
  private Float freezerCapacity;
  private String refrigerant;
  private String temperatureZone;
  private ColdChainEquipmentEnergyType energyType;
  private Long energyTypeId;
  private String energyConsumption;
  private Long maxTemperature;
  private Long minTemperature;
  private Float holdoverTime;
  private String dimension;
  private Float price;
  private Donor donor;
  private Long donorId;
  private ColdChainEquipmentPqsStatus pqsStatus;
  private Long pqsStatusId;

}
