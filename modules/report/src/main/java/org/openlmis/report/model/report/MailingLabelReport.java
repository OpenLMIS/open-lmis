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

/**
 * A report data used to fill mailing labels report.
 * Mailing Labels Report is used to generates mailing labels displaying the contact person and address for each facility in the distribution system.
 * Logistics managers may use these labels when sending reports or corresponds to facilities in the distribution system
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailingLabelReport implements ReportData {

    @Column(name = "F.code")
    private String code;
    @Column(name = "f.name")
    private String facilityName;
    @Column(name = "F.typeid")
    private String facilityType;
    private boolean active;
    @Column(name = "GZ.name")
    private String region;
    @Column(name = "F.address1")
    private String address1;
    private String address2;
    @Column(name = "FO.code")
    private String owner;
    private String email;
    private String phoneNumber;
    private String mslOrmsdCode;
    private String fax;
    private String gpsCoordinates;
    @Column(name = " U.firstName")
    private String contact;

}
