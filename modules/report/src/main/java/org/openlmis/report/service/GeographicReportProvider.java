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
import org.openlmis.report.mapper.lookup.GeographicZoneReportMapper;
import org.openlmis.report.model.GeoZoneReportingRate;
import org.openlmis.report.model.GeoStockStatusFacilitySummary;
import org.openlmis.report.model.GeoStockStatusProductSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class GeographicReportProvider  {

  @Autowired
  GeographicZoneReportMapper geoMapper;

  public List<GeoZoneReportingRate> getReportingRateReport(Long userId,Long programId,Long schedule, Long processingPeriodId){
    return geoMapper.getGeoReportingRate(userId,programId,schedule, processingPeriodId);
  }

    public List<GeoStockStatusFacilitySummary> getStockStatusFacilitySummaryReport(Long programId, Long processingPeriodId, Long productId){
        return geoMapper.getGeoStockStatusFacilitySummary(programId, processingPeriodId,productId);
    }

    public List<GeoStockStatusProductSummary> getStockStatusProductSummaryReport(Long programId, Long geographicZoneId, Long processingPeriodId){
        return geoMapper.getStockStatusProductSummary(programId, geographicZoneId,processingPeriodId);
    }

}
