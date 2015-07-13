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

import javax.persistence.Column;

@Data
@NoArgsConstructor
public class RepairManagementReport implements ReportData {

    @Column(name = "facility_id")
    private  int facility_id;

    @Column(name = "facility_type_id")
    private int facility_type_id;

    @Column(name = "Facility_name")
    private String facility_name;

    @Column(name = "Facility_type")
    private String facility_type;

    @Column(name = "Functional")
    private int functional;

    @Column(name = "Not_Functional")
    private int not_functional;

    @Column(name = "Functional_Not_Installed")
    private int functional_not_installed;

    @Column(name = "Obsolete")
    private  int Obsolete;

    @Column(name = "Waiting_For_Repair")
    private  int Waiting_For_Repair;

    @Column(name = "Waiting_For_Spare_Parts")
    private int Waiting_For_Spare_Parts;

    @Column(name = "Electricity")
    private int electricity;

    @Column(name = "Solar")
    private int solar;

    @Column(name = "Gas")
    private int gas;

    @Column(name = "Kerosene")
    private int kerosene;
}
