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

/**
 * ProgramProductISA represents the attributes to define and calculate ISA for a given product in a program.
 * Also provides method to calculate isaValue on basis of user provided parameters.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public class ProgramProductISA extends BaseModel {

  Long programProductId;

  ISA isa;

  public ProgramProductISA()
  {
    isa = new ISA();
  }

  public Integer calculate(Long population) {
    return isa.calculate(population);
  }


  public Double getWhoRatio() {
    return isa.getWhoRatio();
  }

  public void setWhoRatio(Double whoRatio) {
    isa.setWhoRatio(whoRatio);
  }

  public Integer getDosesPerYear() {
    return isa.getDosesPerYear();
  }

  public void setDosesPerYear(Integer dosesPerYear) {
    isa.setDosesPerYear(dosesPerYear);
  }

  public Double getWastageFactor() {
    return isa.getWastageFactor();
  }

  public void setWastageFactor(Double wastageFactor) {
    isa.setWastageFactor(wastageFactor);
  }

  public Double getBufferPercentage() {
    return isa.getBufferPercentage();
  }

  public void setBufferPercentage(Double bufferPercentage) {
    isa.setBufferPercentage(bufferPercentage);
  }

  public Integer getMinimumValue() {
    return isa.getMinimumValue();
  }

  public void setMinimumValue(Integer minimumValue) {
    isa.setMinimumValue(minimumValue);
  }

  public Integer getMaximumValue() {
    return isa.getMaximumValue();
  }

  public void setMaximumValue(Integer maximumValue) {
    isa.setMaximumValue(maximumValue);
  }

  public Integer getAdjustmentValue() {
    return isa.getAdjustmentValue();
  }

  public void setAdjustmentValue(Integer adjustmentValue) {
    isa.setAdjustmentValue(adjustmentValue);
  }

  public Long getPopulationSource() {
    return isa.getPopulationSource();
  }

  public void setPopulationSource(Long sourceId) {
    isa.setPopulationSource(sourceId);
  }
}
