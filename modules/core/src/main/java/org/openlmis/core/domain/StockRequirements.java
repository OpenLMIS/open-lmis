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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.dto.IsaDTO;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;


@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockRequirements extends BaseModel
{
  Long facilityId;
  Long productId;
  String productCategory;
  String productName;

  ISA isa;

  Double minMonthsOfStock;
  Double maxMonthsOfStock;
  Double eop;

  Long population;

  Integer annualNeed;

  Integer quarterlyNeed;

  Double MinimumStock;

  Double MaximumStock;

  Double ReorderLevel;

  IsaDTO isaDTO;

  int isaValue = 0;

  private Integer getIsaValue()
  {
    if(isa == null || population == null)
      return null;
    return isa.calculate(population);
  }

  private Double getMinimumStock()
  {
    if(getIsaValue() == null || minMonthsOfStock == null)
      return null;
    Double value = getIsaValue() * minMonthsOfStock;
    return MinimumStock = value;
  }

  private Double getMaximumStock()
  {
    if(getIsaValue() == null || maxMonthsOfStock == null)
      return null;
    Double value = getIsaValue() * maxMonthsOfStock;
    return MaximumStock= value;
  }

  private Double getReorderLevel()
  {
    if(getIsaValue() == null || eop == null)
      return null;
    return getIsaValue() * eop;
  }

  private Integer getAnnualNeed(){
    if(getIsaValue() == null)
      return null;
    return getIsaValue() * 12;
  }

  private Integer getQuarterlyNeed(){
    if (getAnnualNeed() == null)
      return null;
    return getAnnualNeed() / 4;
  }


  public String getJSON()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("{");

    builder.append("\"facilityId\": ");
    builder.append(facilityId);

    builder.append(", \"productId\": ");
    builder.append(productId);

    builder.append(", \"productName\": ");
    builder.append("\""+productName +"\"");

    builder.append(", \"productCategory\": ");
    builder.append("\""+productCategory +"\"");

    builder.append(", \"population\": ");
    builder.append(population);

    builder.append(", \"minMonthsOfStock\": ");
    builder.append(minMonthsOfStock);
    builder.append(", \"maxMonthsOfStock\": ");
    builder.append(maxMonthsOfStock);
    builder.append(", \"eop\": ");
    builder.append(eop);

    builder.append(", \"isaCoefficients\": {");
    if(isa != null) {
      builder.append("\"whoRatio\": ");
      builder.append(isa.whoRatio);

      builder.append(", \"dosesPerYear\": ");
      builder.append(isa.dosesPerYear);

      builder.append(", \"wastageFactor\": ");
      builder.append(isa.wastageFactor);

      builder.append(", \"bufferPercentage\": ");
      builder.append(isa.bufferPercentage);

      builder.append(", \"minimumValue\": ");
      builder.append(isa.minimumValue);

      builder.append(", \"maximumValue\": ");
      builder.append(isa.maximumValue);

      builder.append(", \"adjustmentValue\": ");
      builder.append(isa.adjustmentValue);
    }
    builder.append("}");

    builder.append(", \"isaValue\": ");
    builder.append(getIsaValue());

    builder.append(", \"MinimumStock\": ");
    builder.append(getMinimumStock());

    builder.append(", \"MaximumStock\": ");
    builder.append(getMaximumStock());

    builder.append(", \"ReorderLevel\": ");
    builder.append(getReorderLevel());

    builder.append(", \"annualNeed\": ");
    builder.append(getAnnualNeed());

    builder.append(", \"quarterlyNeed\": ");
    builder.append(getQuarterlyNeed());


    builder.append("}");

    return builder.toString();
  }


  //If anyone likes this pattern that this method is intended to support, the method should be generalized and moved elsewhere
  public static String getJSONArray(List<StockRequirements> items)
  {
    StringBuilder builder = new StringBuilder("[");
    for(int i=0; i<items.size(); i++)
    {
      builder.append(items.get(i).getJSON());
      if(i < items.size() - 1)
        builder.append(",");
    }
    builder.append("]");

    return builder.toString();
  }


}
