/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.Product;

import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonSerialize(include = NON_EMPTY)
public class EquipmentLineItem extends LineItem  {

  private Long id;
  private String code;
  private String equipmentName;
  private String equipmentCategory;
  private String equipmentModel;
  private String equipmentSerial;
  private Long equipmentInventoryId;
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
