/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * This class represents a column configured in rnr template for a program.
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RnrColumn extends Column {

  private int position;
  private RnRColumnSource source;
  private Boolean sourceConfigurable;
  private String formula;
  private String indicator;
  private boolean used;
  private boolean mandatory;
  private String calculationOption;


  private FormulaOption options;

  private String description;
  private boolean formulaValidationRequired = true;
  private RnrColumnOption configuredOption;
  private Long createdBy;

  @JsonSerialize(include = NON_EMPTY)
  private List<RnrColumnOption> rnrColumnOptions;

  @SuppressWarnings(value = "unused")
  public void setSourceString(String sourceString) {
    this.source = RnRColumnSource.getValueOf(sourceString);
  }

  @SuppressWarnings(value = "unused")
  public void setOptions(String opt) {
    this.options = new FormulaOption(opt);
  }

  @Override
  public Integer getColumnWidth() {
    if (this.name.equals("product")) {
      return 125;
    }
    if (this.name.equals("remarks")) {
      return 100;
    }
    if (this.name.equals("reasonForRequestedQuantity")) {
      return 100;
    }
    return 40;
  }

  @Override
  public ColumnType getColumnType() {
    if (this.getName().equals("price") || this.getName().equals("cost")) {
      return ColumnType.CURRENCY;
    }
    if (this.getName().equals("product") || this.getName().equals("dispensingUnit") || this.getName().equals("productCode")) {
      return ColumnType.TEXT;
    }
    if (this.getName().equals("skipped")) {
      return ColumnType.BOOLEAN;
    }
    return ColumnType.NUMERIC;
  }


  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (!(o instanceof RnrColumn)) return false;
    RnrColumn rnrColumn = (RnrColumn) o;
    return (this.name.equals(rnrColumn.getName()));
  }

}
