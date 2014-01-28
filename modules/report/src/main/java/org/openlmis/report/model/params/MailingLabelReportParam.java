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
public class MailingLabelReportParam implements ReportData {

    //top filters
    private int facilityTypeId;
    private String facilityType;
    private int rgroupId;
    private String rgroup;

    @Override
    public String toString(){
        if(this == null ) return null;
        StringBuilder filterDescription = new StringBuilder("");
        filterDescription.append("Facility Type : ").append(facilityTypeId != 0 ? facilityType : "All Facility Types ").append("\n").
                         append("Requisition Group : ").append(rgroupId != 0 ? rgroup : "All Requisition Groups");

        return filterDescription.toString().isEmpty() ? null : filterDescription.toString();
    }

}


