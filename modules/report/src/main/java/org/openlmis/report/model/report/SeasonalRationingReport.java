/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT; Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation; either version 2 of the License; or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful; but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not; see http://www.mozilla.org/MPL/
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
public class SeasonalRationingReport implements ReportData {

    private String facilityname;
    private String district;
    private String productname;
    private String adjustmenttype;
    private String adjustmentbasis;
    private Date startdate;
    private Date enddate;
    private int minmonthsofstock;
    private int maxmonthsofstock;
    private String formula;

    private String formatDate(Date date){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM");
            return date == null ? null : simpleDateFormat.format(date);
        }catch(Exception exp){

        }
        return null;
    }

    public String getStartdateString()  {
        return formatDate(this.startdate);
    }

    public String getEnddateString()  {
        return formatDate(this.enddate);
    }
}
