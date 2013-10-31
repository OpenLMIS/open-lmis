/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Facility {

    private Integer id;

    private String code;

    private String name;

    private String description;

    private String gln;

    private String mainPhone;

    private String fax;

    private String address1;

    private String address2;

    private Integer geographicZoneId;

    private Integer typeId;

    private Long catchmentPopulation;

    private Double latitude;

    private Double longitude;

    private Double altitude;

    private Integer operatedById;

    private Double coldStorageGrossCapacity;

    private Double coldStorageNetCapacity;

    private Boolean suppliesOthers;

    private Boolean sdp;

    private Boolean hasElectricity;

    private Boolean online;

    private Boolean hasElectronicScc;

    private Boolean hasElectronicDar;

    private Boolean active;

    private Date goLiveDate;

    private Date goDownDate;

    private Boolean satellite;

    private Integer satelliteParentId;

    private String comment;

    private Boolean dataReportable;
}
