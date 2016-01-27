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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabEquipmentStatusReport  implements ReportData {

    //@Column(name = "facility_name")
    private String facilityName;
   // @Column(name = "equipment_name")
    private String equipmentName;
   // @Column(name = "disrict")
    private String district;
    //@Column(name = "equipment_model")
    private String model;
   // @Column(name = "serial_number")
    private String serialNumber;
   // @Column(name = "equipment_status")
    private String operationalStatus;
    //@Column(name = "equipment_type")
    private String equipmentType;
   // @Column(name = "facility_code")
    private String facilityCode;
   // @Column(name = "facility_type")
    private String facilityType;
   // @Column(name = "zone")
    private String zone;

    //@Column(name = "hasservicecontract")
    private String serviceContract;
   // @Column(name = "contract.name")
    private String vendorName;
   // @Column(name = "contract.contractid")
    private String contractId;

}
