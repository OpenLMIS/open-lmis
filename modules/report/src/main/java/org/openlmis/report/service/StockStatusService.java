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

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.report.mapper.StockStatusMapper;
import org.openlmis.report.model.dto.StockStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class StockStatusService {

  @Autowired
  private StockStatusMapper mapper;

  public List<StockStatusDTO> getStockStatusByQuarter(String programCode, Long year, Long quarter, Long userId){
    return mapper.getStockStatusByQuarter(programCode, year, quarter, userId);
  }


  public List<StockStatusDTO> getStockStatusByMonth(String programCode, Long year, Long quarter, Long userId){
    return mapper.getStockStatusByMonth(programCode, year, quarter, userId);
  }

}
