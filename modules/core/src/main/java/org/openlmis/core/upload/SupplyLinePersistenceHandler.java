/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.service.SupplyLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SupplyLinePersistenceHandler is used for uploads of SupplyLines. It uploads each SupplyLine record by record.
 */
@Component
@NoArgsConstructor
public class SupplyLinePersistenceHandler extends AbstractModelPersistenceHandler {

  SupplyLineService supplyLineService;

  @Autowired
  public SupplyLinePersistenceHandler(SupplyLineService supplyLineService) {
    this.supplyLineService = supplyLineService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    SupplyLine supplyLine = (SupplyLine) record;
    SupplyLine supplyLineFromDB = supplyLineService.getExisting(supplyLine);
    if (supplyLineFromDB != null) {
      if (supplyLineFromDB.getSupplyingFacility().getId().equals(supplyLine.getSupplyingFacility().getId())) {
        setMessageKey("error.duplicate.supply.line");
      } else {
        setMessageKey("error.redundant.warehouse");
      }
    }
    return supplyLineFromDB;
  }

  @Override
  protected void save(BaseModel record) {
    supplyLineService.save((SupplyLine) record);
  }
}