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

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgramProductISA extends BaseModel {

  Long programProductId;
  Double whoRatio;
  Integer dosesPerYear;
  Double wastageFactor;
  Double bufferPercentage;
  Integer minimumValue;
  Integer maximumValue;
  Integer adjustmentValue;

  public Integer calculate(Long population, Integer numberOfMonthsInPeriod, Integer packSize) {
    int isaValue = (int) Math.ceil(population * (this.whoRatio / 100) * this.dosesPerYear * this.wastageFactor / 12 * (1 + this.bufferPercentage / 100) + this.adjustmentValue);

    if (this.minimumValue != null && isaValue < this.minimumValue)
      return this.minimumValue;
    if (this.maximumValue != null && isaValue > this.maximumValue)
      return this.maximumValue;

    Integer idealQuantity = Math.round(isaValue * ((float) numberOfMonthsInPeriod / packSize));
    return idealQuantity < 0 ? 0 : idealQuantity;
  }
}
