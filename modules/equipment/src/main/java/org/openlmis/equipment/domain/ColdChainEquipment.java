/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

  private ColdChainEquipmentDesignation designation;
  private Long designationId;
  private String cceCode;
  private String pqsCode;
  private Float refrigeratorCapacity;
  private Float freezerCapacity;
  private Float capacity;
  private String refrigerant;
  private String temperatureZone;
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
