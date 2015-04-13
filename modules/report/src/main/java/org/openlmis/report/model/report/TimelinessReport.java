
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.report.model.report;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import java.text.SimpleDateFormat;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimelinessReport implements ReportData {

    private Integer zoneId;
    private String district;
    private Integer expected;
    private String depot;
    private String region;
    private Integer reportedOnTime;
    private Integer reportedLate;
    private Integer unscheduled;
    private String facilityName;
    private String facilityTypeName;
    private String facilityCode;
    private String reportingStatus;
    private String status;
    private Date duration;
    private Integer total;
    private Long facilityId;
    private String facility;
    private Integer rnrId;
    private String facilityIds;
    private Integer nonReported;
    private Date reportingStartDate;
    private Date reportingEndDate;
    private Date reportingLateStartDate;
    private Date reportingLateEndDate;

    private String formatDate(Date date){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            return date == null ? null : simpleDateFormat.format(date);
        }
        catch(Exception exp){

        }
        return null;
    }
    public String getDuration(){
        return formatDate(this.duration);
    }
}
