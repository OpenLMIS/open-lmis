/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.model.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;


/**
 * User: Hassan
 * Date: 11/25/13
 * Time: 6:51 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictSummaryReportFilter implements ReportData {

    private int rgroupId;
    private String rgroup;
    private int programId;
    private String program;
    private int scheduleId;
    private String schedule;
    private int periodId;
    private String period;
    private Integer year;
    private Integer zoneId;
    private String zone;
   // private int geographicLevelId;
    //private String geographicLevel;


    @Override
    public String toString(){

        StringBuilder filtersValue = new StringBuilder("");
        filtersValue.append("Period : ").append(this.period).append("\n").
                append("Schedule : ").append(this.schedule).append("\n").
                append("Program : ").append(this.program).append("\n").
               // append("Geographic Level : ").append(this.geographicLevel).append("\n").
                append("District : ").append(this.zone).append("\n");


        return filtersValue.toString();
    }
}
