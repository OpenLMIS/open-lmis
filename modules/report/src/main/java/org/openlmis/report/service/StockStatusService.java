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
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.ProgramProductRepository;
import org.openlmis.report.generator.StockOnHandStatus;
import org.openlmis.report.mapper.StockStatusMapper;
import org.openlmis.report.model.dto.StockStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class StockStatusService {

  private static final String hivProgramCode = "MMIA";
  @Autowired
  private ProgramProductRepository programProductRepository;

  @Autowired
  private StockStatusMapper mapper;

  public List<StockStatusDTO> getStockStatusByQuarter(String programCode, Long year, Long quarter, Long userId){
    return mapper.getStockStatusByQuarter(programCode, year, quarter, userId);
  }


  public List<StockStatusDTO> getStockStatusByMonth(String programCode, Long year, Long quarter, Long userId){
    return mapper.getStockStatusByMonth(programCode, year, quarter, userId);
  }

  public StockOnHandStatus getStockOnHandStatus(long cmm, long soh, String productCode) {
    return getStockOnHandStatus(cmm, soh, isHivProject(productCode));
  }

  public StockOnHandStatus getStockOnHandStatus(long cmm, long soh, boolean isHiv) {
      if (0 == soh) {
          return StockOnHandStatus.STOCK_OUT;
      }
      if (cmm == -1) {
          return StockOnHandStatus.REGULAR_STOCK;
      }

      if (soh < 1 * cmm) {
          return StockOnHandStatus.LOW_STOCK;
      } else if ((!isHiv && soh > 2 * cmm) || (isHiv && soh > 3 * cmm)) {
          return StockOnHandStatus.OVER_STOCK;
      }
      return StockOnHandStatus.REGULAR_STOCK;
  }

  private Boolean isHivProject(String productCode) {
    List<ProgramProduct> programProducts = programProductRepository.getByProductCode(productCode);
    for(ProgramProduct programProduct : programProducts) {
      if(null != programProduct.getProgram().getParent()) {
        return programProduct.getProgram().getParent().getCode().equals(hivProgramCode);
      }
      return programProduct.getProgram().getCode().equals(hivProgramCode);
    }
    return false;
  }

  //maxOccurredDateEntry.estimated_months
    // = (maxOccurredDateEntry.cmm === -1.0 || maxOccurredDateEntry.cmm === 0) ?
    // undefined : Math.floor(10 * maxOccurredDateEntry.soh / maxOccurredDateEntry.cmm) / 10;

  public Double calcMos(double cmm, int soh) {
      if (-1.0 == cmm || 0 == cmm) {
          return null;
      }
      return Math.floor(10 * soh / cmm) / 10;
  }
}
