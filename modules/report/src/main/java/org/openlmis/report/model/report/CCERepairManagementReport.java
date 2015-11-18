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

import lombok.*;
import org.openlmis.report.model.ReportData;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
public class CCERepairManagementReport implements ReportData {

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
