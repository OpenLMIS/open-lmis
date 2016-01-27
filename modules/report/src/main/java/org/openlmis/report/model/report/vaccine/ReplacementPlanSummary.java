
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

package org.openlmis.report.model.report.vaccine;

import lombok.*;
import org.openlmis.report.model.ReportData;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplacementPlanSummary implements ReportData {


    private String serialNumber;
    private String sourceOfEnergy;
    private String equipment;
    private Integer ReplacementYear;
    private Integer referenceYear;
    private Integer replacementCost;
    private Integer totalEquipment;

    private Integer total;
    private Integer breakDown;
    private String workingStatus;
    private String status;
    private float purchasePrice;

    private String district;
    private String region;
    private String brand;
    private Integer regionId;
    private String model;
    private Integer Capacity;

    private String facilityName;
    private String facilityTypeName;
    private Integer facilityId;
    private Integer total_year1;
    private Integer total_year2;
    private Integer total_year3;
    private Integer total_year4;
    private Integer total_year5;
    private float this_year_cost;
    private Integer replacementYearOne;
    private Integer replacementYearTwo;
    private Integer replacementYearThree;
    private Integer replacementYearFour;
    private Integer replacementYearFive;




}
