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

package org.openlmis.core.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderQuantityAdjustmentProduct extends BaseModel {

  private Facility facility;
  private Product product;
  private OrderQuantityAdjustmentType adjustmentType;
  private OrderQuantityAdjustmentFactor adjustmentFactor;
  private Date startDate;
  private Date endDate;
  private Long minMOS;
  private Long maxMOS;
  private String formula;
  private String description;


  @SuppressWarnings("unused")
  public String getStringStartDate() throws ParseException {
    return this.startDate == null ? null : new SimpleDateFormat("dd-MM-yyyy").format(this.startDate);
  }

  @SuppressWarnings("unused")
  public String getStringEndDate() throws ParseException {
    return this.endDate == null ? null : new SimpleDateFormat("dd-MM-yyyy").format(this.endDate);
  }


}
