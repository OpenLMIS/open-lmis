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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * The ISA class contains the coefficients and calculation necessary to determine an ISA (Ideal Stock Amount).
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
public class ISA extends BaseModel
{

  Double whoRatio;
  Integer dosesPerYear;
  Double wastageFactor;
  Double bufferPercentage;
  Integer minimumValue;
  Integer maximumValue;
  Integer adjustmentValue;

  private ISA(Builder builder)
  {
    this.whoRatio = builder.whoRatio;
    this.dosesPerYear = builder.dosesPerYear;
    this.wastageFactor = builder.wastageFactor;
    this.bufferPercentage = builder.bufferPercentage;
    this.minimumValue = builder.minimumValue;
    this.maximumValue = builder.maximumValue;
    this.adjustmentValue = builder.adjustmentValue;
  }

  public Integer calculate(Long population)
  {
    int isaValue = (int) Math.ceil(population * (this.whoRatio / 100) * this.dosesPerYear * this.wastageFactor / 12 * (1 + this.bufferPercentage / 100) + this.adjustmentValue);

    if (this.minimumValue != null && isaValue < this.minimumValue)
      return this.minimumValue;
    if (this.maximumValue != null && isaValue > this.maximumValue)
      return this.maximumValue;

    return isaValue;
  }

  public static class Builder
  {
    private Double whoRatio;
    private Integer dosesPerYear;
    private Double wastageFactor;
    private Double bufferPercentage;
    private Integer minimumValue;
    private Integer maximumValue;
    private Integer adjustmentValue;

    public Builder whoRatio(Double value)
    {
      this.whoRatio = value;
      return this;
    }

    public Builder dosesPerYear(Integer value)
    {
      this.dosesPerYear = value;
      return this;
    }

    public Builder wastageFactor(Double value)
    {
      this.wastageFactor = value;
      return this;
    }

    public Builder bufferPercentage(Double value)
    {
      this.bufferPercentage = value;
      return this;
    }

    public Builder minimumValue(Integer value)
    {
      this.minimumValue = value;
      return this;
    }

    public Builder maximumValue(Integer value)
    {
      this.maximumValue = value;
      return this;
    }

    public Builder adjustmentValue(Integer value)
    {
      this.adjustmentValue = value;
      return this;
    }

    public ISA build()
    {
      return new ISA(this);
    }
  }
}
