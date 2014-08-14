/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.ReportParameter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
public class AdjustmentSummaryReportParam
        extends BaseParam implements ReportParameter {

    private Long period;
    private ProcessingPeriod periodObject;

    private Long programId;
    private Program programObject;

    private Long facilityId;
    private int facilityTypeId;
    private String facilityType;
    private Long zoneId;
    private String productId;
    private int productCategoryId;

    private String  adjustmentTypeId;
    private String adjustmentType;

    SimpleDateFormat dateFormatter = new SimpleDateFormat();

    @Override
    public String toString(){
         if(periodObject != null){

            StringBuilder filtersValue = new StringBuilder("");

            filtersValue.append("Program: ")
                .append(programObject.getName())
                .append("\nPeriod: ")
                .append(periodObject.getName())
                .append(", ").append(periodObject
                .getStringYear())
                .append("\n")
                //.append("Facility Types : ").append(this.getFacilityType()).append("\n").
                .append("Adjustment Types: ")
                .append(this.getAdjustmentType());
//                .append("\n")
//                .append("Reporting Groups : ")
//                .append(this.getRgroup());

            return filtersValue.toString();
         }   else{
           return "No filters selected";
         }
    }
}
