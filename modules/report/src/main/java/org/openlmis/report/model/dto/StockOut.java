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

package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: Issa
 * Date: 3/17/14
 * Time: 1:49 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockOut {
    private String facilityName;
    private String facilityCode;
    private Long requisitionGroupId;
    private Long rnrId;
    private Long geographicZoneId;
    private String product;
    private String location;
    private Integer totalStockOut;
    private Integer totalGeoFacility;
    private Boolean suppliedInPast;
    private Double mosSuppliedInPast;

}
