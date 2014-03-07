/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.repository;

import org.openlmis.distribution.domain.EpiInventory;
import org.openlmis.distribution.domain.EpiInventoryLineItem;
import org.openlmis.distribution.repository.mapper.EpiInventoryLineItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repository class for epi inventory related database operations.
 */

@Repository
public class EpiInventoryRepository {

  @Autowired
  private EpiInventoryLineItemMapper mapper;

  public void save(EpiInventory epiInventory) {
    for (EpiInventoryLineItem lineItem : epiInventory.getLineItems()) {
      if (lineItem.getId() != null) {
        mapper.updateLineItem(lineItem);
      } else {
        mapper.insertLineItem(lineItem);
      }
    }
  }

  public EpiInventory getBy(Long facilityVisitId) {
    return new EpiInventory(mapper.getLineItemsBy(facilityVisitId));
  }
}
