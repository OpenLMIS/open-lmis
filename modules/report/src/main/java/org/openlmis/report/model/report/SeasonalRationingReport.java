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
