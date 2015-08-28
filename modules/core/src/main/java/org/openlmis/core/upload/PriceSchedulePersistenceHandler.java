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

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.ProductPriceSchedule;
import org.openlmis.core.service.ProductPriceScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * PriceSchedulePersistenceHandler is used for uploads of PriceSchedule. It uploads each PriceSchedule
 * record by record.
 */
@Component
@NoArgsConstructor
public class PriceSchedulePersistenceHandler extends AbstractModelPersistenceHandler {


  private ProductPriceScheduleService priceScheduleService;

  @Autowired
  public PriceSchedulePersistenceHandler(ProductPriceScheduleService priceScheduleService) {
    this.priceScheduleService = priceScheduleService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return priceScheduleService.getByProductCodePriceScheduleCategory((ProductPriceSchedule) record);
  }

  @Override
  protected void save(BaseModel record) {
      priceScheduleService.save((ProductPriceSchedule) record);
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.price.schedule";
  }

}
