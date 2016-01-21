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
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductPriceSchedule extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "Product code", nested = "code")
  private Product product;

  @ImportField(mandatory = true, name = "Price Schedule", nested = "code")
  private PriceSchedule priceSchedule;

  @ImportField(mandatory = false, name = "Product name")
  private String productName;

  @ImportField(mandatory = false, name = "Price")
  private Double price;

}
