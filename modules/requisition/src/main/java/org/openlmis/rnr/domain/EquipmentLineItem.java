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

package org.openlmis.rnr.domain;

import lombok.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.Product;

import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

@Getter
@Setter
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class EquipmentLineItem extends LineItem  {

  private String code;
  private String equipmentName;
  private String equipmentCategory;
  private String equipmentModel;
  private String equipmentSerial;
  private Long equipmentInventoryId;
  private Long inventoryStatusId;
  private Long operationalStatusId;
  private Long testCount;
  private Long totalCount;
  private Long daysOutOfUse;

  private Long programEquipmentId;

  private Boolean enableTestCount;
  private Boolean enableTotalCount;

  private List<Product> relatedProducts;

  private String remarks;

  @Override
  public boolean compareCategory(LineItem lineItem) {
    return false;
  }

  @Override
  public String getCategoryName() {
    return null;
  }

  @Override
  public String getValue(String columnName) throws NoSuchFieldException, IllegalAccessException {
    return null;
  }

  @Override
  public boolean isRnrLineItem() {
    return false;
  }
}
