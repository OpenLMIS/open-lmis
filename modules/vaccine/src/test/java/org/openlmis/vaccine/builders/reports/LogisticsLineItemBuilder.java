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

package org.openlmis.vaccine.builders.reports;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.vaccine.domain.reports.DiseaseLineItem;
import org.openlmis.vaccine.domain.reports.LogisticsLineItem;

public class LogisticsLineItemBuilder {

  public static final Instantiator<LogisticsLineItem> defaultLogisticsLineItem = new Instantiator<LogisticsLineItem>() {

    @Override
    public LogisticsLineItem instantiate(PropertyLookup<LogisticsLineItem> lookup) {
      LogisticsLineItem item = new LogisticsLineItem();

      item.setProductId(1L);
      item.setProductName("The Product");
      item.setProductCategory("Vaccine");
      item.setProductCode("PCode");
      item.setDisplayOrder(1);
      return item;
    }
  };
}
