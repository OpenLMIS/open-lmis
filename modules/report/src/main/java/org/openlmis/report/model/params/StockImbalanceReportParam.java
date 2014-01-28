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
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockImbalanceReportParam implements ReportData {

    private int facilityTypeId;
    private String facilityType;
    private int productId;
    private String product;
    private int productCategoryId;
    private String productCategory;
    private int rgroupId;
    private String rgroup;
    private String facility;
    private int programId;
    private String program;
    private int scheduleId;
    private String schedule;
    private int periodId;
    private String period;
    private Integer year;

    @Override
    public String toString(){

        StringBuilder filtersValue = new StringBuilder("");
        filtersValue.append("Period : ").append(this.period).append("\n").
                append("Schedule : ").append(this.schedule).append("\n").
                append("Program : ").append(this.program).append("\n").
                append("Product Category : ").append(this.productCategory).append("\n").
                append("Product : ").append(this.product).append("\n").
                append("Facility Types : ").append(this.getFacilityType()).append("\n").
                append("Reporting Groups : ").append(this.getRgroup());

        return filtersValue.toString();
    }
}
